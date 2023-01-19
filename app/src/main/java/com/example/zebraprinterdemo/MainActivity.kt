package com.example.zebraprinterdemo

import android.app.Activity
import android.content.IntentFilter
import android.app.PendingIntent
import android.hardware.usb.UsbManager
import com.zebra.sdk.printer.discovery.DiscoveredPrinterUsb
import android.content.BroadcastReceiver
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Parcelable
import android.os.Bundle
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import com.zebra.sdk.printer.discovery.UsbDiscoverer
import android.widget.Toast
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.UsbConnection
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import java.lang.Exception
import java.util.*

class MainActivity : Activity() {
    private val filter = IntentFilter(ACTION_USB_PERMISSION)
    private var mPermissionIntent: PendingIntent? = null
    private var hasPermissionToCommunicate = false
    private var mUsbManager: UsbManager? = null
    private var buttonRequestPermission: Button? = null
    private var buttonPrint: Button? = null
    private var buttonSerial: Button? = null
    private var discoveredPrinterUsb: DiscoveredPrinterUsb? = null

    // Catches intent indicating if the user grants permission to use the USB device
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register broadcast receiver that catches USB permission intent
        mUsbManager = getSystemService(USB_SERVICE) as UsbManager
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        buttonRequestPermission = findViewById<View>(R.id.button1) as Button
        buttonPrint = findViewById<View>(R.id.button2) as Button
        buttonSerial = findViewById<View>(R.id.button3) as Button

        // Request Permission button click
        buttonRequestPermission!!.setOnClickListener {
            Thread { // Find connected printers
                val handler: UsbDiscoveryHandler = UsbDiscoveryHandler()
                UsbDiscoverer.findPrinters(applicationContext, handler)
                try {
                    while (!handler.discoveryComplete) {
                        Thread.sleep(100)
                    }
                    if (handler.printers != null && handler.printers!!.size > 0) {
                        discoveredPrinterUsb = handler.printers!![0]
                        if (!mUsbManager!!.hasPermission(discoveredPrinterUsb!!.device)) {
                            mUsbManager!!.requestPermission(
                                discoveredPrinterUsb!!.device,
                                mPermissionIntent
                            )
                        } else {
                            hasPermissionToCommunicate = true
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        e.message + e.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.start()
        }

        // Print button click
        buttonPrint!!.setOnClickListener {
            if (hasPermissionToCommunicate) {
                var conn: Connection? = null
                try {
                    conn = discoveredPrinterUsb!!.connection
                    conn.open()
                    conn.write("~WC".toByteArray())
                } catch (e: ConnectionException) {
                    Toast.makeText(
                        applicationContext,
                        e.message + e.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    if (conn != null) {
                        try {
                            conn.close()
                        } catch (e: ConnectionException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "No permission to communicate",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        buttonSerial!!.setOnClickListener {
            if (hasPermissionToCommunicate) {
                var usbConn: UsbConnection? = null
                try {
                    Log.d("Main Activity", mUsbManager.toString())
                    Log.d("Main Activity", discoveredPrinterUsb!!.device.toString())
                    usbConn = UsbConnection(mUsbManager, discoveredPrinterUsb!!.device)
                    Log.d("Main Activity", usbConn.serialNumber)
                } catch (e: ConnectionException) {
                    Toast.makeText(
                        applicationContext,
                        e.message + e.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    if (usbConn != null) {
                        try {
                            usbConn.close()
                        } catch (e: ConnectionException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "No permission to communicate",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPause() {
        unregisterReceiver(mUsbReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mUsbReceiver, filter)
    }

    // Handles USB device discovery
    internal inner class UsbDiscoveryHandler : DiscoveryHandler {
        var printers: MutableList<DiscoveredPrinterUsb>?
        var discoveryComplete = false

        init {
            printers = LinkedList()
        }

        override fun foundPrinter(printer: DiscoveredPrinter) {
            printers!!.add(printer as DiscoveredPrinterUsb)
        }

        override fun discoveryFinished() {
            discoveryComplete = true
        }

        override fun discoveryError(message: String) {
            discoveryComplete = true
        }
    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }
}