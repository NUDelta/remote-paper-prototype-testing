# Remote Paper Prototype Testing
Authored by [Kevin Chen](http://kevinchen.ninja), 2015.

Work done as part Delta Lab's Design, Technology, and Research (DTR) program, advised by Prof. Haoqi Zhang.

## Premise
Remote Paper Prototype Testing, or RPPT, takes a new approach to lo-fidelity prototype evaluation. Traditional advances in prototyping or design methods lean toward quickly developing more complex and fleshed out demos, which often means creating many predefined interactions for designers to customize and insert. RPPT instead focuses on making low fidelity paper prototyping, which has no such restrictions, more accessible, powerful, and able to be conducted remotely. For further information, take a look at [our CHI 2015 publication.](http://dl.acm.org/citation.cfm?id=2702423)

## Components Applications & Setup
See [a demo video](https://drive.google.com/file/d/0B2XFICitZbqybmgtU29RaVdpWEE/view?usp=sharing) of an older version of this setup in action. Take a look at each component's respective README for further details and usage.

### Tech Stack at a Glance
 * Meteor
  * ES6, MongoDB, jQuery-qrcode
 * iOS
  * Swift 2, ObjectiveDDP
 * Android
  * Java, Android-DDP
 * Tokbox

### [Web Application](https://github.com/NUDelta/rppt-web)
Designers and wizards will use the web application as a sort of "command center" to observe user tester actions and set tasks. This consists of syncing information (number and QR code for iOS and Glass respectively), a first-person perspective stream from Google Glass, location information from the iOS application, and a video stream sent **to** the iOS application with overlayed tap gestures.

This last stream is meant to stream a feed of the paper prototype you intend to be testing with your users. We recommend connecting a webcam to the computer with the web application and point it at the paper prototype. Here was our initial setup:

[picture here]

You'll want to either deploy this application (either on your own servers or use `meteor deploy`), use our example deployed instance (`http://rppt.meteor.com`), or setup internet sharing so your iOS and Glass applications can connect to `localhost` (use `meteor` to run the application in this case).

We use [OpenTok](http://tokbox.com) for the video streaming components. You'll want to sign up for a developer account (there's a 30 day free trial), create a project, and enter your API and secret keys into `server/lib/environment.js`.

### [iOS Application](https://github.com/NUDelta/rppt-ios)
Your user testers will use this application to view a video stream of the paper prototype coming from the web application. All gestures they use to interact with the video stream will be displayed over top on the web side. Any task updates in the web task setter will be shown at the bottom of the app.

Be sure to update the `endpoint` variable in the `AppDelegate.swift` file with the location of where you chose to deploy your server, following the format already there (e.g. `ws://[your-server-location]/websocket`).

### [Google Glass Application](https://github.com/NUDelta/rppt-glass)
Your user testers will wear Glass running this application to provide you will a first person perspective video stream of what they're doing while using your application.

Like the iOS Application, be sure to update the `METEOR_URL` variable in `MainActivity.java` with where your server is deployed.

## Next Steps
### Polish
We've got some number of TODOs on the side of error handling and general usability updates to this setup. Please open up issues if you come across anything :)

### More Exciting Stuff
We're looking into more opportunities to add more *mixed-fidelity* components to try and simplify the designer/wizard's job in this process. One idea we're planning to work on is to create *widgets*, by replacing colored pieces of paper in the prototype with software counterparts.

## Contact
[kevinchen2016@u.northwestern.edu](mailto:kevinchen2016@u.northwestern.edu)
