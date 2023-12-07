# term-project-rdbrooks-meball-jzdzilow-sridley-

Here is a little breakdown of the API requests you can make:

http://localhost:3333/session?command=begin → begins the session

http://localhost:3333/session?command=end → ends the session

http://localhost:3333/addHelpRequester?name=_&email=_&bugType= → add a help requester by name

http://localhost:3333/helpRequesterDone?name=_&email=_&record= → remove a help requester by name

http://localhost:3333/helpRequesterDone?name=_&email=_&record=no → remove a help requester by name and remove them from the list tracking attendance

http://localhost:3333/addDebuggingPartner?name=_&email=_ → add a debugging partner by name

http://localhost:3333/debuggingPartnerDone?name=_&email=_&record= → remove a debugging partner by name

http://localhost:3333/debuggingPartnerDone?name=_&email=_&record=no → remove a debugging partner by name and remove them from the list tracking attendance

http://localhost:3333/getInfo → gets general information on the current state of the collab hours manager

http://localhost:3333/getInfo?role=debuggingPartner&name=_&email=_ → gets info on a specific debugging partner by name

http://localhost:3333/getInfo?role=helpRequester&name=_&email=_ → gets info on a specific help requester by name

http://localhost:3333/escalate?helpRequesterName=_&helpRequesterEmail=_ → escalate a help requester by name

http://localhost:3333/flagAndRematch?helpRequesterName=_&helpRequesterEmail=_&debuggingPartnerName=_&debuggingPartnerEmail=_ → rematch and flag debugging partner and help requester by name

http://localhost:3333/isInstructor?email= → determine if a given email is considered a TA based on csv
