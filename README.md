<p align="center">
  <img width="" height="" src="https://camo.githubusercontent.com/3304d6a9fa86ba25fbbf14d7b31bb44382733b3292411f3f16ffa956e5c1a8a0/68747470733a2f2f646f63732e77656270616765746573742e6f72672f696d672f7770742d6e6176792d6c6f676f2e706e67">
</p>

[Learn about more WebPageTest API Integrations in our docs](https://docs.webpagetest.org/api/integrations/#officially-supported-integrations)

#### Steps to follow in integrating WPT & Slack

##### 1. Setting up a java project.
  * Clone this repository.
  * Update bot_token,channel_id, ngrokUrl in application.properties file (bottoken="", channelid="", ngrokurl="")

 We have currently two APIs to serve requests
* To trigger slack modal on command execution. This path will be used for slash commands in slack (ngrokUrl/submittest)
* To generate the response coming from webpagetest as view and post on slack. This path will be used for interactivity URL (ngrokUrl/test)
* /testresult: Internally this API is used to fetch test results and ping back once result is ready.
  
  Note: We are using ngrok to setup a public URL against your localhost(e.g.8080) which is then used as a pingback URL followed by /testresult.

##### 2. Making locally running server publicly accessible
 * Install and start ngrok
 * After installing ngrok run "ngrok http 8080"
 * This returns public URL which forwards request to localhost(e.g. 8080), on which our server is running.
 * Now our server is publicly accessible by the URLs returned by ngrok.

##### 4. Create new slack app>> From Scratch

 ![image](https://user-images.githubusercontent.com/81590480/119186520-d288b480-ba95-11eb-9a7b-ff0e91db5968.png)


 * OK, you’ve created it! Now what? Well, we’re going to want to install the Slack App into your workspace and to install this in your workspace we have to add at least one feature or permission scope for the app. Click on the “permission scope” link there (alternatively, you can click on OAuth & Permissions in the sidebar. You’ll see Scopes on your page next.


 * Add any Scope: e.g. you can give chat:write, nce chat:write is added, scroll up. You should see a button where you can Install App to Workspace . If you don’t for some reason, you can navigate to Install Apps on the left sidebar.


 ![image](https://user-images.githubusercontent.com/81590480/119186606-f3e9a080-ba95-11eb-82da-1bd9cd3c0613.png)


 * After Re-Installing the app to your workspace, you can see OAuth tokens are added under user and bot token: Please take bot_OAuth_token and paste it under application.properties file


 ![image](https://user-images.githubusercontent.com/81590480/119186695-12e83280-ba96-11eb-8f65-6ca7adf5d8ef.png)
 
 
 * Slash Command, make sure to add api path of triggering slack URL modal. (https://ngrokUrl/submittest)
 
 slash command image
 
 * Next is adding an interactivity URL, any interactions with modals or interactive components are sent to this URL. Add the second APIs URL to post the message once response from WPT is fetched. (https://ngrokUrl/test)
 
 interactivity image
 
 * Run the command on slack:
     Next step is to check our command on slack, start by typing webpagetest, you should see a recommendation of webpagetest as below.
     
     slack slash command image
     
 * Above command should open a modal like below
 
    modal view image
 
 * Once all these things are done, run your application and hot slash command from slack ('/').You will get your desired results in your slack channel once pingback url has successfully captured test results  

<img width="540" alt="slack" src="https://user-images.githubusercontent.com/81590480/122232219-b2071b00-ced8-11eb-87ba-28db9e007610.PNG">

