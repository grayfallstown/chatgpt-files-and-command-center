# Installation Guide for Fedora

The Goal of this guide is to make the ChatGPT File And Command Center available
over the internet, so ChatGPT can actually use it. We will make the application
reachable, create a DNS name for it and secure it with https. Feel free to ask
ChatGPT for additional help with this.

## Configure Firewall to Allow Ports 8080, 80, and 443

1. Open a terminal.
2. Check if FirewallD is installed by running: `sudo firewall-cmd --state`
3. If FirewallD is installed, allow necessary ports:
    ```
    sudo firewall-cmd --permanent --add-port=8080/tcp
    sudo firewall-cmd --permanent --add-port=80/tcp
    sudo firewall-cmd --permanent --add-port=443/tcp
    ```
4. Reload the firewall: `sudo firewall-cmd --reload`

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

1. Install Certbot: `sudo dnf install certbot`
2. Run Certbot to obtain a certificate: `sudo certbot certonly --manual`
3. Follow the prompts to create a new certificate for your domain.
4. The certificate will be saved in a specified directory.

## Setup OpenSSL

1. Install OpenSSL if not installed: `sudo dnf install openssl`

## Convert the Certificate for Use with Spring Boot

1. Open Terminal and navigate to the directory containing your certificate.
2. Run the following commands to convert your certificate to PKCS#12 format:
    ```
    openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name myalias
    ```
3. Enter a password when prompted.

## Wire Up the Certificate with Spring Boot

1. Place the `keystore.p12` file in ChatGPT File And Command Center `./src/main/resources/`
2. Copy `docs/example_application.yml` to `./src/main/resources/application.yml` and change
   the following lines:
    ```
    server:
      ssl:
        key-store-password: your-password
        key-alias: myalias
    springdoc:
      server:
        url: https://yourdnsname/
    ```
1. Start ChatGPT File And Command Center by running `./mvnw spring-boot:run`.

Your Spring Boot application should now be accessible via HTTPS on port 443.