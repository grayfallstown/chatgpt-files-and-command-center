# Custom GPT Setup Guide

## Calling the OpenAPI Schema from the Application with No-IP Subdomain

1. Ensure your application is running and accessible via the No-IP subdomain.
2. Use the following URL format to call the OpenAPI schema:
    ```
    https://<your-subdomain>/v3/api-docs
    ```
3. Replace `<your-subdomain>` with your actual No-IP subdomain.
4. You should see the OpenAPI schema in JSON format, if not click in your browser on raw data.
5. Save this json to a file, you will need it.

## Navigating to Custom GPT Creation in ChatGPT Web Interface

1. Log in to the ChatGPT web interface.
2. Navigate to the **Custom GPT** section.
3. Click on **Create Custom GPT**.

## Pasting the OpenAPI Schema

1. In the Custom GPT creation interface, find the section to upload or paste your OpenAPI schema.
2. Copy the OpenAPI schema from the URL (as mentioned above) and paste it into the provided field.
3. It might show an error message, but if you scroll down you should see file api action, then everything is fine.

## Using the Prompt from `custom-gpt-prompt.md`

1. Open the [premade Custom GPT prompt](./custom-gpt-prompt.md).
2. Copy the entire content of this file.
3. Feel free to customize the prompt to fit your needs.
4. In the Custom GPT creation interface, find the section to configure the prompt.
5. Paste the copied prompt into the provided field.
6. Give it the API key from your current project (apikey.txt) in the chat on the right.
7. Tell it to call the systeminfo endpoint to check if everything works.

## Finishing the Custom GPT Setup

1. Complete the remaining configuration steps as required by the ChatGPT interface.
2. **Do not publish** the custom GPT. It is connected with your Computer. Keeping it unpublished ensures that it remains private and only accessible to you.

## Starting a Chat with the Custom GPT

1. Once your Custom GPT is set up, navigate to the Chats section in the ChatGPT interface.
2. Select your custom GPT from the list of available custom GPTs and start a new chat, just say hi.
3. When prompted for an API key, use the key generated for your project. You can find this key in your `apikey.txt` file.
4. You can have as many projects as you like, each has their own API key and with each chat, it asks you for the API key to use.

## Troubleshooting

### Testing OpenAPI Schema Accessibility

1. Use an online curl service to test if the OpenAPI schema is accessible via your No-IP subdomain:
    ```
    curl https://<your-subdomain>/v3/api-docs
    ```
2. Ensure that the schema is returned correctly in JSON format.

### Testing DNS Propagation

1. Use online DNS propagation checking tools to ensure your No-IP subdomain is properly propagated.
2. If the DNS has not propagated, wait for a few minutes and test again.

If you encounter any issues, refer to the application logs and ensure all configurations are set correctly.

### When ChatGPT suddenly has issus using the API in the middle of a chat.

1. Make sure the ChatGPT File and Command Center is currently running
2. Remind it of the correct API key to use and to add it to the URL path.
3. Investigate the log of the application (console, where you started it, or ./logs/chatgpt-command-and-control-center.log)
