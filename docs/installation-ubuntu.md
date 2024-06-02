# Installation Guide for Ubuntu

The Goal of this guide is to make the ChatGPT File And Command Center available
over the internet, so ChatGPT can actually use it. We will make the application
reachable, create a DNS name for it and secure it with https. Feel free to ask
ChatGPT for additional help with this.

## Configure Firewall to Allow Ports 8080, 80, and 443

1. Open a terminal.
2. Check if UFW is installed by running: `sudo ufw status`
3. If UFW is installed, allow necessary ports:
    ```
    sudo ufw allow 8080
    sudo ufw allow 80
    sudo ufw allow 443
    ```
4. Enable UFW: `sudo ufw enable`

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
5. Download and install the No-IP DUC (Dynamic Update Client) for Linux.
6. Log in to the DUC with your No-IP account credentials.
7. Configure the DUC to update your hostname with your current IP address.

## Get a Certificate with Let's Encrypt

1. Install Certbot: `sudo apt install certbot`
2. Run Certbot to obtain a certificate: `sudo certbot certonly --manual`
3. Follow the prompts to create a new certificate for your domain.
4. The certificate will be saved in a specified directory.

## Setup OpenSSL

1. Install OpenSSL if not installed: `sudo apt install openssl`

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