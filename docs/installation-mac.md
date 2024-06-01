# Installation Guide for macOS

## Configure Firewall to Allow Ports 8080, 80, and 443

1. Check if the firewall is enabled by opening System Preferences and going to Security & Privacy.
2. Click on the Firewall tab, then click the lock icon to make changes.
3. Click on Firewall Options or Advanced.
4. Click the Add (+) button to add a new rule.
5. Select the app or service, and set the ports to 8080, 80, and 443. Click OK.

## Setup Port Forwarding on a Router

1. Access your router’s web interface by entering its IP address in a web browser.
2. Log in with your username and password.
3. Find the port forwarding section, often located under Advanced settings or similar.
4. Create a new port forwarding rule:
    - Name: HTTP and HTTPS
    - Internal IP: Your computer’s IP address
    - Ports to forward: 8080, 80, 443
    - Protocol: TCP
    - Save the settings.

## Get a Dynamic DNS with No-IP

1. Create an account at [No-IP](https://www.noip.com/).
2. Confirm your account via email.
3. Log in and navigate to `Dynamic DNS` > `Create Hostname`.
4. Choose a hostname and domain, then click `Add Hostname`.
5. Download and install the No-IP DUC (Dynamic Update Client) for macOS.
6. Log in to the DUC with your No-IP account credentials.
7. Configure the DUC to update your hostname with your current IP address.

## Get a Certificate with Let's Encrypt

1. Install Homebrew if you haven't already: `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"`
2. Install Certbot: `brew install certbot`
3. Run Certbot to obtain a certificate: `sudo certbot certonly --manual`
4. Follow the prompts to create a new certificate for your domain.
5. The certificate will be saved in a specified directory.

## Setup OpenSSL

1. Install OpenSSL using Homebrew: `brew install openssl`
2. Add the OpenSSL bin directory to your system PATH if necessary.

## Convert the Certificate for Use with Spring Boot

1. Open Terminal and navigate to the directory containing your certificate.
2. Run the following commands to convert your certificate to PKCS#12 format:
    ```
    openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name myalias
    ```
3. Enter a password when prompted.

## Wire Up the Certificate with Spring Boot

1. Place the `keystore.p12` file in your Spring Boot project’s resources directory.
2. Add the following properties to your `application.yml`:
    ```
    server:
      port: 443
      ssl:
        key-store: classpath:keystore.p12
        key-store-password: your-password
        key-store-type: PKCS12
        key-alias: myalias
    ```
3. Restart your Spring Boot application.

Your Spring Boot application should now be accessible via HTTPS on port 443.