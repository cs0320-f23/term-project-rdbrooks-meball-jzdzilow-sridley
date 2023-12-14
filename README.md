# term-project-rdbrooks-meball-jzdzilow-sridley-

# Collab Section Manager

Description: The aim of this project is to facilitate CS0320 collaboration hours. When registered teaching assistants log onto the platform, they will be redirected to the instructor page, where they can start, end, and manage a session of collab section. Students can log in and choose to be debugging partners or help requesters, and from there, they will be paired in the order of arrival. These students can they escalate, submit debugging questions, and see other updates to their session. This project can ease this experience for students and TAs, and make collab hours more productive.

[Here](https://github.com/cs0320-f23/term-project-rdbrooks-meball-jzdzilow-sridley) is our repo.

# Team Members

- Rachel Brooks (rdbrooks): @rdbrooks
- Megan Ball (meball): @helium-balloon
- Julia Zdzilowska (jzdzilow): @jzdzilowska
- Sarah Ridley (sridley): @22ridley

Total Estimated Time: 100 hours combined

# Design Choices

## Frontend:

On the frontend, users start by logging in to the site with Firebase. If the user's email address is listed as a TA after a call to the backend, then the user is redirected to the instructor page. Otherwise, as long as their email address is a Brown address, they are allowed to select their student role, as a Debugging Partner or a Help Requester.

We use React route mapping to direct users to these different pages. Once these users are on their respective pages, they can do all of the necessary actions associated with their role. Instructors can begin, end, and manage a session of collab section as well as download attendance information. Help requesters can join the queue of help requesters, and when they are matched, they can see who their debugging partner is. Debugging partners can join the queue of debugging partners, and when they are matched, they can also see who their help requester is, escalate their help requester, and submit debugging question answers. Debugging partners can only leave after 60 minutes of being a debugging partner.

We have our major components in the components directory, and we have our CSS style sheets in the styles directory. We also have a recoil directory that contains the recoil variables that we use across the project.

## Backend:

Our backend is split up into a debuggingPartner directory, a handlers directory, a helpRequester directory, a server directory, a sessionState directory, and a utils directory. The main class is in the server/Server file. 

In the debuggingPartner directory, there is the DebuggingPartner class, which represents a debugging partner student, and the DebuggingPartnerQueue class, which stores debugging partners and keeps track of their order. The helpRequester does the same for help requester students.

The handlers directory contains all of the endpoint handlers that deal with server requests. The server directory contains the HoursDispatcher, which deals with pairing debugging partners and help requesters. The Server class has the main entry point. The main class in Server runs the server, which accepts requests to endpoints and runs the dispatcher, which handles pairing debugging partners and help requesters.

The sessionState directory contains the CsvWriter class with deals with writing attendance data into the data directory in the back folder. The SessionState class handles metadata for a session, like start time, end time, and more. 

Finally, the utils directory contains the Utils class for time utility methods.

Here is a breakdown of the API requests you can make directly to the backend endpoints:

- http://localhost:3333/session?command=begin → begins the session
- http://localhost:3333/session?command=end → ends the session
- http://localhost:3333/addHelpRequester?name=_&email=_&bugType= → add a help requester by name
- http://localhost:3333/helpRequesterDone?name=_&email=_&record= → remove a help requester by name
- http://localhost:3333/helpRequesterDone?name=_&email=_&record=no → remove a help requester by name and remove them from the list tracking attendance
- http://localhost:3333/addDebuggingPartner?name=_&email=_ → add a debugging partner by name
- http://localhost:3333/debuggingPartnerDone?name=_&email=_&record= → remove a debugging partner by name
- http://localhost:3333/debuggingPartnerDone?name=_&email=_&record=no → remove a debugging partner by name and remove them from the list tracking attendance
- http://localhost:3333/getInfo → gets general information on the current state of the collab hours manager
- http://localhost:3333/getInfo?role=debuggingPartner&name=_&email=_ → gets info on a specific debugging partner by name
- http://localhost:3333/getInfo?role=helpRequester&name=_&email=_ → gets info on a specific help requester by name
- http://localhost:3333/escalate?helpRequesterName=_&helpRequesterEmail=_ → escalate a help requester by name
- http://localhost:3333/flagAndRematch?helpRequesterName=_&helpRequesterEmail=_&debuggingPartnerName=_&debuggingPartnerEmail=_ → rematch and flag debugging partner and help requester by name
- http://localhost:3333/isInstructor?email= → determine if a given email is considered a TA based on csv
- http://localhost:3333/submitDebuggingQuestions?debuggingPartnerName=_&debuggingPartnerEmail=_&helpRequesterName=_&helpRequesterEmail=_&bugCategory=_&debuggingProcess=_ → submit debugging questions
- http://localhost:3333/downloadInfo?type=\_ → downloads CSV info based on type (all, debugging, helpRequester)

# Errors/Bugs

As far as we know, there are no remaining bugs in our program. There are a few testing intricacies, which we explain in the "How to... run tests" section.

# Tests

## Frontend:

For frontend testing, we used Playwright to check that the UI elements of each page appear properly and are usable. Unfortunately, we did not have sufficient time to investigate how to run several different interactive Playwright sessions to mock different users logging on in different roles to be matched by the Collab Section Manager. Instead, we manually tested these features my having one person log into the application with several different email addresses and selecting different roles in different browsers.

## Backend: 

For backend testing, we used Junit to unit test and integration test our backend server and code. First, we unit tested all of the non-handler classes, including classes that represent debugging partners, help requesters, the queues for each, and more. Then, we tested all of the handlers and endpoints available from the server. Finally, for integration testing, we placed these in the TestIntegration file in the handlers test directory. 

Currently, the integration tests have the @Test tag commented out, because they cause the mvn test command to fail. This is because, to test integration with a server and a dispatcher, the route mapping is completed differently from the regular unit tests that we run with mvn test. Still, with these integration tests, we demonstrate that help requesters and debugging partners are matched in the right order, can leave, can be flagged, and more.

# How to...

## Build and Run the Program

Unfortunately, after many hours of effort, we were unable to use Render or Heroku to deploy the backend of our program. As such, we run this application locally.

To build and run the program, you must run the backend and frontend separately. If you do not run the backend, the frontend login will fail!

To run the frontend, `cd front` to get into the front directory. Then run `npm install` to install dependencies, and then `npm start` to run the frontend locally in port 3000 by default.

To run the backend, `cd back` to get into the back directory. Then run `mvn compile exec:java` to run the backend locally in port 3333 by default.

## Run the Tests

To run the frontend tests, `cd front` to get into the front directory. Then run `npx playwright install`, then `npx playwright test` or `npx playwright test --ui` to run the playwright tests.

To run the backend tests, `cd back` to get into the back directory. Then run `mvn clean` to clean the target directory and `mvn test` to run the backend tests. To run the integration tests, you can run these by un-commenting the @Test tags and then using the green arrow play button to run them individually.