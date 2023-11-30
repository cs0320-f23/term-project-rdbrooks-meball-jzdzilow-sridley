# term-project-rdbrooks-meball-jzdzilow-sridley-

Here is a little breakdown of the API requests you can make:

http://localhost:3333/session?command=begin --> begins the session

http://localhost:3333/session?command=end --> ends the session

http://localhost:3333/addHelpRequester?helpRequester= --> add a help requester by name

http://localhost:3333/helpRequesterDone?helpRequester= --> remove a help requester by name

http://localhost:3333/addDebuggingPartner?debuggingPartner= --> add a debugging partner by name

http://localhost:3333/debuggingPartnerDone?debuggingPartner= --> remove a debugging partner by name

http://localhost:3333/getInfo --> gets general information on the current state of the collab hours manager

http://localhost:3333/getInfo?role=debuggingPartner&name= --> gets info on a specific debugging partner by name

http://localhost:3333/getInfo?role=helpRequester&name= --> gets info on a specific help requester by name

http://localhost:3333/escalate?helpRequester= --> escalate a help requester by name
