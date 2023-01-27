# Zebra GK420d Printer Demo

## Required Hardware
- Android Tablet with USB interface (Sunmi T2s used in testing)
- [Zebra GK420d printer](https://www.amazon.com/Zebra-Receipts-Barcodes-Parallel-Connectivity/dp/B00EUN90SG?pd_rd_w=3Ntqo&content-id=amzn1.sym.deffa092-2e99-4e9f-b814-0d71c40b24af&pf_rd_p=deffa092-2e99-4e9f-b814-0d71c40b24af&pf_rd_r=4NBRKZ2GFCN150VDYQVV&pd_rd_wg=oivWY&pd_rd_r=e3841e04-3540-4777-a572-2fb1c1169f9b&pd_rd_i=B00EUN90SG&psc=1&ref_=pd_bap_d_grid_rp_0_16_i) with USB Cable and Power Cable
- [2.25 x 4 Thermal Paper](https://www.amazon.com/Thermal-Perforated-Shipping-Compatible-Printers/dp/B07QY3LJ6R?pd_rd_w=3Ntqo&content-id=amzn1.sym.deffa092-2e99-4e9f-b814-0d71c40b24af&pf_rd_p=deffa092-2e99-4e9f-b814-0d71c40b24af&pf_rd_r=4NBRKZ2GFCN150VDYQVV&pd_rd_wg=oivWY&pd_rd_r=e3841e04-3540-4777-a572-2fb1c1169f9b&pd_rd_i=B07QY3LJ6R&ref_=pd_bap_d_grid_rp_0_5_i&th=1)

## Setup
Follow these steps to deploy and test the demo application.
  1. Download or clone the project
  2. Open the project in Android Studio
  3. Build the APK
  4. Install the APK on the target device. To do this on the Sunmi follow [these steps](https://sunmi-1.atlassian.net/wiki/spaces/NARHOWTO/pages/1795981317/Turn+Desktop+Device+to+Debug+Mode) to enable USB debugging. For other tablets, enable developer options and USB debugging to allow for USB deployments through Android Studio.
  
## Usage
Once the app is installed on the target device, a single activity will show the three action buttons\
First you must press "Request USB Permission" to allow the app to access USB interfaces.\
Then you can press "Print" to print the test ticket. You can also press "Get Serial Number" to log the serial number of your device to Logcat.

## Approach
The application implements a single activity to demonstrate a successful USB interface with the printer. On activity creation, we initialize button listeners for requesting USB permission, printing a test ticket, and querying the printer serial number. We use the Link-OS Library provided by Zebra for discovering printers then USB Manager for handling permissions. Once permission to communicate is established, we can open the printer connection and send ZPL language commands to define the print format and initiate a print. The ZPL language commands are sent as a byte array to the printer where they are parsed and acted upon. If we do not have USB permission, we cannot print!

## Author
Anthony Rizzo\
anthony@trycents.com
