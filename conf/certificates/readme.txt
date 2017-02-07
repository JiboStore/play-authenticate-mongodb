For IGG push server written in PHP:
source: https://github.com/immobiliare/ApnsPHP/blob/master/Doc/CertificateCreation.md
use server_certificates_bundle_sandbox.p12 and server_certificates_bundle_sandbox.pem in folder server_certificates_bundle_sandbox
password is empty

To generate a certificate on a Mac OS X:

Log-in to the iPhone Developer Program Portal
Choose App IDs from the menu on the right (or click here)
Create an App ID without a wildcard. For example 3L223ZX9Y3.it.immobiliare.labs.apnsphp
Click the Configure link next to this App ID and then click on the button to start the wizard to generate a new Development Push SSL Certificate (Apple Documentation: Creating the SSL Certificate and Keys)
Download this certificate and double click on aps_developer_identity.cer to import it into your Keychain
Launch Keychain Assistant (located in Application, Utilities or search for it with Spotlight) and click on My Certificates on the left
Expand Apple Development Push Services and select Apple Development Push Services AND your private key (just under Apple Development Push Services)
Right-click and choose "Export 2 elements..." and save as server_certificates_bundle_sandbox.p12 (don't type a password).
Open Terminal and change directory to location used to save server_certificates_bundle_sandbox.p12 and 1. convert the PKCS12 certificate bundle into PEM format using this command (press enter when asked for Import Password):

openssl pkcs12 -in server_certificates_bundle_sandbox.p12 -out server_certificates_bundle_sandbox.pem -nodes -clcerts

Now you can use this PEM file as your certificate in ApnsPHP!

For notnoop java apns lib:
use export_single_selection_root.p12 in folder server_certificates_bundle_sandbox
How to generate: (screenshot is provided)
1. download the .cer from member center
2. install .cer to mac
3. open keychain and single select the root of the certificate (do not select the private key)
4. export and SPECIFY password, as oracle jdk doesn't support passwordless .p12 file
5. use the .p12 as in the program