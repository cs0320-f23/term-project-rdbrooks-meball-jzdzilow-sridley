import { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import {
  IssueType,
  UserRole,
  mockedMode,
  singleSessionState,
  userSessionState,
} from "../recoil/atoms";
import "../styles/Dashboard.css";
import { useNavigate } from "react-router-dom";
import Timer from "./Timer";
import "../styles/nightsky.scss";
import { IUser } from "../types/IUser";
//import * as FileSaver from "file-saver";

const Dashboard = () => {
  const [userSession, setUserSession] = useRecoilState(userSessionState);
  const [singleSession, setSingleSession] = useRecoilState(singleSessionState);
  const navigate = useNavigate();
  const [bugCategory, setBugCategory] = useState("");
  const [debuggingProcess, setDebuggingProcess] = useState("");
  // timer for debugger to leave
  const [fullTimeRemaining, setFullTimeRemaining] = useState(
    calculateFullTimeRemaining
  );
  // timer for escalation
  const [pairedTime, setPairedTime] = useState(0);
  const [escalationTimeRemaining, setEscalationTimeRemaining] = useState(
    calculateTimeBeforeEscalation
  );
  const [sessionStarted, setSessionStarted] = useState(false);

  // call to get info from backend will continuously update this information
  const [unpairedDP, setUnpairedDP] = useState([]);
  const [unpairedHR, setUnpairedHR] = useState([]);
  const [escalatedPairs, setEscalatedPairs] = useState([]);
  const [nonEscalatedPairs, setNonEscalatedPairs] = useState([]);

  const [escalationResult, setEscalationResult] = useState("");
  const isMockedMode = useRecoilValue(mockedMode);

  // resets user session back to log in on unwanted backend interruptions
  useEffect(() => {
    if (userSession.user === null) {
      setSingleSession({
        partner: null,
        issueType: IssueType.NoneSelected,
      });
      setUserSession({ user: null, role: UserRole.NoneSelected, time: null });
      navigate("/login");
    }
  }, [userSession.user]);

  // resets user session back to log in on session ended
  useEffect(() => {
    if (userSession.user?.role !== "instructor") {
      const fetchData = async () => {
        try {
          await fetch("http://localhost:3333/getInfo")
            .then((response) => response.json())
            .then((data) => {
              // if successfully can get info (and thus session is running), set values appropriately
              if (data["result"] === "success" && !sessionStarted) {
                setSessionStarted(true);
              }
              // if no session is running, sets all values to empty arrays
              if (
                data["result"] === "error_bad_request" &&
                data["error_message"] === "No session is running."
              ) {
                setSingleSession({
                  partner: null,
                  issueType: IssueType.NoneSelected,
                });
                setUserSession({
                  user: null,
                  role: UserRole.NoneSelected,
                  time: null,
                });
              }
            });
        } catch (error) {
          console.log("Error encountered: " + error);
        }
      };
      // fetches data initally
      fetchData();
      // Fetch data every 5 seconds
      const intervalId = setInterval(fetchData, 5000);
      return () => clearInterval(intervalId);
    }
  }, []);

  /* MOCKED BACKEND -------------------------------------- */

  useEffect(() => {
    if (isMockedMode) {
      const fetchPartner = async () => {
        try {
          const response = await fetch("http://localhost:2000/getSession");
          const data = await response.json();
          if (userSession.role === UserRole.DebuggingPartner) {
            const sessionAsDP = data.pHelpRequester;
            const partner = sessionAsDP.user;
            const issue = sessionAsDP.issueType;
            setSingleSession({ partner: partner, issueType: issue });
          }
          if (userSession.role === UserRole.HelpRequester) {
            const sessionAsHR = data.pDebuggingPartner;
            const partner = sessionAsHR.user;
            setSingleSession({
              partner: partner,
              issueType: singleSession.issueType,
            });
          }
        } catch (error) {
          console.log("Error encountered in mocked mode: " + error);
        }
      };
      fetchPartner();
    }
  }, []);

  /* end of MOCKED BACKEND -------------------------------------- */

  /* -------------------------------timer content ---------------------------------------*/

  // calculates time remaining for a session
  function calculateFullTimeRemaining() {
    if (userSession.time === null) {
      return 0;
    }

    const oneHourInMillis = 60 * 60 * 1000; // 1 hour in milliseconds
    const currentTime = new Date().getTime();
    const elapsedTime = currentTime - userSession.time.getTime();
    const remainingTime = Math.max(oneHourInMillis - elapsedTime, 0);
    return remainingTime;
  }

  useEffect(() => {
    const timerInterval = setInterval(() => {
      setFullTimeRemaining(calculateFullTimeRemaining());
    }, 1000);

    return () => clearInterval(timerInterval);
  }, [userSession.time]);

  // timer for escalation (15 minutes)
  function calculateTimeBeforeEscalation() {
    if (userSession.time === null) {
      return 0;
    }
    const fifteenMinsInMillis = 1 * 60 * 1000; // 15 mins in milliseconds
    const currentHour = new Date().getHours();
    const currentMin = new Date().getMinutes();
    const currentSec = new Date().getSeconds();

    const millisecondsHour = 60 * 60 * 1000; // 1 hour in milliseconds
    const millisecondsInMinute = 60 * 1000; // 1 minute in milliseconds
    const millisecondsInSecond = 1000; // 1 sec in milliseconds

    const currTimeMilis =
      currentHour * millisecondsHour +
      currentMin * millisecondsInMinute +
      currentSec * millisecondsInSecond;

    // change userSession.time.getTime() to something that changes when people are matched
    console.log(currTimeMilis, pairedTime);
    const elapsedTime = currTimeMilis - pairedTime;
    const remainingTime = Math.max(fifteenMinsInMillis - elapsedTime, 0);
    return remainingTime;
  }

  useEffect(() => {
    const timerInterval = setInterval(() => {
      setEscalationTimeRemaining(calculateTimeBeforeEscalation());
    }, 1000);

    return () => clearInterval(timerInterval);
  }, [singleSession]);

  /* ---------------------------- end of timer content ------------------------------------*/

  /* -------------------------- get info for instructors ----------------------------------*/

  /* gets information about debugging parters, help requesters and pairings from 
  backend to update instructor page */
  useEffect(() => {
    const fetchData = async () => {
      try {
        await fetch("http://localhost:3333/getInfo")
          .then((response) => response.json())
          .then((data) => {
            // if successfully can get info (and thus session is running), set values appropriately
            if (data["result"] === "success") {
              setUnpairedDP(data.openDBPs);
              setUnpairedHR(data.waitingHRQs);
              setEscalatedPairs(data.escalatedPairs);
              setNonEscalatedPairs(data.nonEscalatedPairs);
            }
            // if no session is running, sets all values to empty arrays
            if (
              data["result"] === "error_bad_request" &&
              data["error_message"] === "No session is running."
            ) {
              setUnpairedDP([]);
              setUnpairedHR([]);
              setEscalatedPairs([]);
              setNonEscalatedPairs([]);
            }
          });
      } catch (error) {
        console.log("Error encountered when fetching session data: " + error);
      }
    };

    // fetches data initally
    fetchData();

    // Fetch data every 5 seconds (adjust the interval as needed)
    const intervalId = setInterval(fetchData, 5000);
    return () => clearInterval(intervalId);
  }, []);

  /* -------------------------- end of get info for instructors ---------------------------*/

  /* ---------------------------------- get info debuggings and help requesters -----------------------------------------*/

  // get info for debugging helpers and help requesters every 5 seconds
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        if (userSession.role === UserRole.DebuggingPartner) {
          await fetch(
            "http://localhost:3333/getInfo?role=debuggingPartner&name=" +
              userSession.user?.name +
              "&email=" +
              userSession.user?.email
          )
            .then((response) => response.json())
            .then((data) => {
              if (data["result"] === "error_bad_request") {
                setUserSession({
                  user: null,
                  role: UserRole.NoneSelected,
                  time: null,
                });
              }
              // create partner
              const partnerName = data.helpRequesterName;
              const partnerEmail = data.helpRequesterEmail;
              const bugType = data.helpRequesterBug;
              const flagged: boolean = data.flagged;
              if (flagged) {
                setSingleSession({
                  partner: null,
                  issueType: IssueType.NoneSelected,
                });
                return alert("You have been flagged!");
              }
              const pairedAtTimeString = data.pairedAtTime;
              const [hours, minutes, seconds] = pairedAtTimeString
                .split(":")
                .map(Number);
              const millisecondsInHour = 60 * 60 * 1000; // 1 hour in milliseconds
              const millisecondsInMinute = 60 * 1000; // 1 minute in milliseconds
              const millisecondsInSecond = 1000; // 1 second in milliseconds

              // to fix problem of backend not being 24 hour clock
              var adjustHours = hours;

              // add 12 hours to the hours from the backend when in the 24 hr clock it's 13+
              if (new Date().getHours() > 12) {
                adjustHours += 12;
              }

              // adjust hours will be the hours when 0-12
              // adjust hours will add 12 hours to backend when 13-24
              const pairedAtTimeMilis =
                adjustHours * millisecondsInHour +
                minutes * millisecondsInMinute +
                seconds * millisecondsInSecond;

              if (partnerName === "") {
                setSingleSession({
                  partner: null,
                  issueType: IssueType.NoneSelected,
                });
              }
              if (partnerName !== "") {
                setPairedTime(pairedAtTimeMilis);
                var partner: IUser = {
                  email: partnerEmail,
                  name: partnerName,
                  role: "student",
                };
                if (bugType === "bug") {
                  setSingleSession({
                    partner: partner,
                    issueType: IssueType.Bug,
                  });
                }
                if (bugType === "conceptual") {
                  setSingleSession({
                    partner: partner,
                    issueType: IssueType.ConceptualQuestion,
                  });
                }
              }
            });
        }
        if (userSession.role === UserRole.HelpRequester) {
          await fetch(
            "http://localhost:3333/getInfo?role=helpRequester&name=" +
              userSession.user?.name +
              "&email=" +
              userSession.user?.email
          )
            .then((response) => response.json())
            .then((data) => {
              if (data["result"] === "error_bad_request") {
                setUserSession({
                  user: null,
                  role: UserRole.NoneSelected,
                  time: null,
                });
              }
              const partnerName = data.debuggingPartnerName;
              const escalated = data.escalated;
              if (escalated) {
                setEscalationResult("You have been escalated");
              }
              if (partnerName === "") {
                setSingleSession({
                  partner: null,
                  issueType: IssueType.NoneSelected,
                });
              }

              if (partnerName !== "") {
                var partner: IUser = {
                  email: "",
                  name: partnerName,
                  role: "student",
                };

                setSingleSession({
                  partner: partner,
                  issueType: singleSession.issueType,
                });
              }
            });
        }
      } catch (error) {
        console.log("Error encountered during fetching user data" + error);
      }
    };

    // fetches data initally
    fetchUserData();

    // Fetches data every 5 seconds
    const intervalId = setInterval(() => fetchUserData(), 5000);
    return () => clearInterval(intervalId);
  }, []);

  function removeFromQueue(
    email: string | undefined,
    name: string | undefined,
    role: UserRole
  ): Promise<String> {
    if (role === UserRole.DebuggingPartner) {
      console.log(name);
      console.log(email);
      return fetch(
        "http://localhost:3333/debuggingPartnerDone?name=" +
          name +
          "&email=" +
          email +
          "&record=yes"
      )
        .then((response) => response.json())
        .then((data) => {
          return data["result"];
        })
        .catch((error) => {
          console.log(
            "Error encountered when removing debugging partner from queue" +
              error
          );
        });
    }
    // if user is a help requester and they have been paired
    if (role === UserRole.HelpRequester && singleSession.partner) {
      return fetch(
        "http://localhost:3333/helpRequesterDone?name=" +
          name +
          "&email=" +
          email +
          "&record=yes"
      )
        .then((response) => response.json())
        .then((data) => {
          return data["result"];
        })
        .catch((e) => {
          return "ERROR: " + e;
        });
    }
    // if user is help requester and they haven't been paired
    if (role === UserRole.HelpRequester && singleSession.partner == null) {
      return fetch(
        "http://localhost:3333/helpRequesterDone?name=" +
          name +
          "&email=" +
          email +
          "&record=no"
      )
        .then((response) => response.json())
        .then((data) => {
          return data["result"];
        })
        .catch((error) => {
          console.log(
            "Error encountered when removing help requester from queue" + error
          );
        });
    } else {
      return new Promise<String>((resolves) => {
        resolves(
          "ERROR: UserRole must be HelpRequester or DebuggingPartner for this function"
        );
      });
    }
  }

  /* ---------------------------------- end get info debuggings and help requesters -----------------------------------------*/

  const openResourcesWebsite = () => {
    const url: string = "https://hackmd.io/@brown-csci0320/BJKCtyxxs/";
    window.open(url, "_blank");
  };

  /* ---------------------------------- handlers -----------------------------------------*/

  const handleFormSubmit = async () => {
    console.log(mockedMode);
    if (bugCategory === "" || debuggingProcess === "") {
      return alert("Bug category and debugging process inputs required!");
    }
    if (!singleSession.partner) {
      alert(
        "You cannot submit this form until you have been matched with a help requester"
      );
    }
    if (userSession.user && singleSession.partner) {
      console.log(
        userSession.user.email,
        singleSession.partner,
        bugCategory,
        debuggingProcess
      );
      try {
        if (isMockedMode) {
          const response = await fetch("http://localhost:2000/submitForm/", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              data: {
                user: userSession.user.email,
                partner: singleSession.partner.email,
                bugCategory: bugCategory,
                debuggingProcess: debuggingProcess,
              },
            }),
          });

          if (!response.ok) {
            // If the server returns an error status
            throw new Error("Server error: " + response.status);
          } else {
            const result = await response.json();
            if (result.success) {
              setSingleSession({
                partner: null,
                issueType: IssueType.NoneSelected,
              });
              setBugCategory("");
              setDebuggingProcess("");
            } else {
              // Handle other success scenarios or display an error message
              alert("Form submission failed:" + result);
            }
          }
        } else {
          // THIS WILL NOT WORK YET DON"T HAVE EMAIL OF HELP REQUESTER
          // need to get the info about who is submitting
          const response = await fetch(
            "http://localhost:3333/submitDebuggingQuestions?debuggingPartnerName=" +
              userSession.user.name +
              "&debuggingPartnerEmail=" +
              userSession.user.email +
              "&helpRequesterName=" +
              singleSession.partner.name +
              "&helpRequesterEmail=" +
              singleSession.partner.email +
              "&bugCategory=" +
              bugCategory +
              "&debuggingProcess=" +
              debuggingProcess
          )
            .then((response) => response.json())
            .then((data) => {
              if (data.result === "success") {
                // SUBMITTING THE FORM DOESN"T RESET THE DEBUGGING PARTNER; ONLY "I'M DONE" BY THE HELP REQUESTER
                // setSingleSession({
                //   partner: null,
                //   issueType: IssueType.NoneSelected,
                // });
                setBugCategory("");
                setDebuggingProcess("");
                console.log("success");
              }
            });
        }
      } catch (error) {
        console.log("Error encountered during form submission" + error);
      }
    }
  };

  const handleEscalate = async () => {
    try {
      console.log(singleSession.partner?.name, singleSession.partner?.email);
      await fetch(
        "http://localhost:3333/escalate?helpRequesterName=" +
          singleSession.partner?.name +
          "&helpRequesterEmail=" +
          singleSession.partner?.email
      )
        .then((response) => response.json())
        .then((data) => {
          if (data.result === "success") {
            setEscalationResult("Escalation Success");
          } else {
            setEscalationResult("Escalation failed");
          }
        });
    } catch (error) {
      console.error("ERROR: " + error);
    }
  };

  const handleEndSession = (role: UserRole) => {
    if (role === UserRole.DebuggingPartner) {
      removeFromQueue(
        userSession.user?.email,
        userSession.user?.name,
        UserRole.DebuggingPartner
      );
    }
    if (role === UserRole.HelpRequester) {
      console.log(userSession);
      removeFromQueue(
        userSession.user?.email,
        userSession.user?.name,
        UserRole.HelpRequester
      );
      // reset partner of debugging partner

      // take help requester out of the queue
    }
    setBugCategory("");
    setDebuggingProcess("");
    setSingleSession({ partner: null, issueType: IssueType.NoneSelected });
    setUserSession({ user: null, role: UserRole.NoneSelected, time: null });
  };

  // starts the session by calling backend
  const handleStart = async () => {
    try {
      await fetch("http://localhost:3333/session?command=begin");
    } catch (error) {
      console.log("Error encountered during session start: " + error);
    }
    setSessionStarted(true);
  };

  // ends the session by calling backend
  const handleEnd = async () => {
    try {
      // end session
      await fetch("http://localhost:3333/session?command=end");

      const downloadInfoResponse = await fetch(
        "http://localhost:3333/downloadInfo?type=debugging"
      );

      if (downloadInfoResponse.ok) {
        // if success response
        const csvBlob = await downloadInfoResponse.blob(); // create blob element
        const downloadLink = document.createElement("a"); // create link
        downloadLink.href = URL.createObjectURL(csvBlob); // url for blob, set as href attribute

        // get current date for filename
        const currentDate = new Date();
        const formattedDateTime =
          currentDate.toLocaleDateString() +
          "-" +
          currentDate.toLocaleTimeString();

        // Set file name for download
        downloadLink.download =
          "debugging-attendance-" + formattedDateTime + ".csv";
        document.body.appendChild(downloadLink); // add link to document body
        downloadLink.click(); // trigger click on link
        document.body.removeChild(downloadLink); // remove link from document body
      } else {
        console.log("ERROR: could not download");
      }
    } catch (error) {
      console.log("Error encountered during session end" + error);
    }
    setSessionStarted(false);
  };

  // removes a debugging partner from attendance (button accessible to instructors)
  const handleRemove = (name: string, email: string) => async () => {
    console.log("TEST");
    return fetch(
      "http://localhost:3333/debuggingPartnerDone?name=" +
        name +
        "&email=" +
        email +
        "&record=no"
    )
      .then((response) => response.json())
      .then((data) => {
        return data["message"];
      })
      .catch((error) => {
        console.log(
          "Error encountered during debugging partner removal: " + error
        );
      });
  };

  // will rematch the help requester and flag the debugging partner by removing from attendance
  const handleRematchFlag =
    (HRname: string, HRemail: string, DPname: string, DPemail: string) =>
    async () => {
      return fetch(
        "http://localhost:3333/flagAndRematch?helpRequesterName=" +
          HRname +
          "&helpRequesterEmail=" +
          HRemail +
          "&debuggingPartnerName=" +
          DPname +
          "&debuggingPartnerEmail=" +
          DPemail
      )
        .then((response) => response.json())
        .then((data) => {
          return data["message"];
        })
        .catch((error) => {
          console.log("Error encountered during rematching: " + error);
        });
    };

  console.log("session started " + sessionStarted);

  const handleDownloadAll = async () => {
    await fetch("http://localhost:3333/downloadInfo?type=all", {
      method: "GET",
    })
      .then((response) => {
        if (!response.ok) {
          alert("HTTP error! Status: " + response.status);
        }
        const filenameHeader = response.headers.get("Content-Disposition");
        const filename = filenameHeader
          ? filenameHeader.split("=")[1]
          : "all-attendance.csv";
        console.log(filename);
        console.log(filenameHeader);
        return response.blob().then((blob) => ({ blob, filename }));
      })
      .then(({ blob, filename }) => {
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      })
      .catch((error) => {
        console.log("Error encountered during data download: " + error);
      });
  };

  /* ----------------------- end of handlers / below rendering ---------------------------*/

  const renderHeaderBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return (
          <header className="instructor-header">
            <button className="download-button" onClick={handleDownloadAll}>
              Download All Data
            </button>
            {!sessionStarted ? (
              <button className="start-button" onClick={handleStart}>
                Start Session
              </button>
            ) : (
              <button className="end-button" onClick={handleEnd}>
                End Session
              </button>
            )}
          </header>
        );
      case UserRole.DebuggingPartner:
        return (
          <header className="user-header">
            <p className="join-time">
              Join time: {userSession.time?.toLocaleTimeString()}
            </p>
            {}
            {renderTimerOrButton()}
          </header>
        );
      case UserRole.HelpRequester:
        return (
          <header className="user-header" style={{ marginBottom: 170 }}>
            <p className="join-time">
              Join time: {userSession.time?.toLocaleTimeString()}
            </p>
            {}
            <button
              className="done-button"
              onClick={() => handleEndSession(UserRole.HelpRequester)}
            >
              I'm done!
            </button>
          </header>
        );
      default:
        return null;
    }
  };

  const renderTimerOrButton = () => {
    // full time remaining = 60
    if (fullTimeRemaining > 0) {
      return <Timer fullTimeRemaining={fullTimeRemaining} />;
    } else if (fullTimeRemaining === 0) {
      return (
        <button
          className="done-button"
          onClick={() => handleEndSession(UserRole.DebuggingPartner)}
        >
          I'm done!
        </button>
      );
    }
  };

  const renderEscalateTimerOrButton = () => {
    if (!singleSession.partner) {
      return "Single session hasn't started!";
    } else if (escalationTimeRemaining > 0) {
      return <Timer fullTimeRemaining={escalationTimeRemaining} />;
    } else if (escalationTimeRemaining === 0) {
      return (
        // <button className="escalate-button" onClick={() => handleEscalate()}>
        //   Escalate!
        // </button>
        <div>
          <button className="escalate-button" onClick={handleEscalate}>
            {" "}
            Escalate!{" "}
          </button>
          {escalationResult && <p>{escalationResult}</p>}
        </div>
      );
    }
  };

  // creation of instructor page
  const renderInstructorContent = () => {
    return (
      <div className="instructor-container">
        <div className="unpaired-students-container">
          <div className="general-title">
            <b>Debugging Partners:</b>
          </div>
          <div className="list-debugging" style={{ height: "95px" }}>
            {/*checks if there are unpaired debugging partners to determine what to dispaly*/}
            {unpairedDP && unpairedDP.length > 0 ? (
              unpairedDP.map((partner, index) => (
                <div key={index} className="single-debugging">
                  <div className="single-debugging-namentime">
                    {/*displays debugging partner name*/}
                    <p className="name">
                      {index + 1}. {partner[0]}
                    </p>
                    {/*displays join time*/}
                    <p className="time">Joined at {partner[2]}</p>
                  </div>
                  {/*button to remove from attendance*/}
                  <button onClick={handleRemove(partner[0], partner[1])}>
                    Remove
                  </button>
                </div>
              ))
            ) : (
              <div
                style={{
                  marginBottom: "10px",
                  color: "darkred",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                None available!
              </div>
            )}
          </div>
          <div className="general-title">
            <b>Help Requesters:</b>
          </div>{" "}
          <div className="list-debugging">
            {/*checks if there are unpaired help requester to determine what to display*/}
            {unpairedHR && unpairedHR.length > 0 ? (
              unpairedHR.map((partner, index) => (
                <div
                  key={index}
                  className="single-debugging"
                  style={{ display: "flex", justifyContent: "flex-start" }}
                >
                  <div className="single-debugging-namentime">
                    {/*displays help requester name*/}
                    <p className="name">
                      {index + 1}. {partner[0]}
                    </p>
                    {/*displays join time*/}
                    <p className="time">Joined at {partner[2]}</p>
                  </div>
                </div>
              ))
            ) : (
              <div
                style={{
                  marginBottom: "10px",
                  color: "darkred",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                None in queue!
              </div>
            )}
          </div>
        </div>
        <div className="paired-students-container">
          <div className="general-title">
            <b>Escalated Pairs:</b>
          </div>{" "}
          <div className="list-debugging" style={{ height: "95px" }}>
            {/*checks if there are escalated pairs to determine what to display*/}
            {escalatedPairs && escalatedPairs.length > 0 ? (
              escalatedPairs.map((pair, index) => (
                <div key={index} className="single-debugging">
                  <div className="single-debugging-namentime">
                    {/*displays names of pair*/}
                    <p className="name">
                      {index + 1}. {pair[0][0]} & {pair[1][0]}
                    </p>
                    {/*displays time of match*/}
                    <p className="time">Matched at {pair[2][0]}</p>
                  </div>
                  {/*button to rematch help requester and flag debugging partner*/}
                  <button
                    onClick={handleRematchFlag(
                      pair[1][0],
                      pair[1][1],
                      pair[0][0],
                      pair[0][1]
                    )}
                  >
                    Rematch and Flag
                  </button>
                </div>
              ))
            ) : (
              <div
                style={{
                  color: "darkred",
                  display: "flex",
                  justifyContent: "center",
                }}
              >
                None yet!
              </div>
            )}
          </div>
          <div className="general-title">
            <b>Non-Escalated Pairs:</b>
          </div>{" "}
          <div className="list-debugging">
            {/*checks if there are non-escalated pairs to determine what to display*/}
            {nonEscalatedPairs && nonEscalatedPairs.length > 0 ? (
              nonEscalatedPairs.map((pair, index) => (
                <div key={index} className="single-debugging">
                  <div className="single-debugging-namentime">
                    {/*displays names of pair*/}
                    <p className="name">
                      {index + 1}. {pair[0][0]} & {pair[1][0]}
                    </p>
                    {/*displays time of match*/}
                    <p className="time">Matched at {pair[2][0]}</p>
                  </div>
                  <button
                    onClick={handleRematchFlag(
                      pair[1][0],
                      pair[1][1],
                      pair[0][0],
                      pair[0][1]
                    )}
                  >
                    Rematch and Flag
                  </button>
                </div>
              ))
            ) : (
              <div
                style={{
                  marginBottom: "10px",
                  color: "darkred",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                None yet!
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  const renderContentBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        // to make code easier to read put instructor content in seperate section
        return renderInstructorContent();
      case UserRole.DebuggingPartner:
        return (
          <div className="debugging-partner-content">
            <div className="other-partner-info">
              <p>
                Your help requester is:{" "}
                <b>
                  {" "}
                  {singleSession.partner
                    ? singleSession.partner.name
                    : "No one yet!"}
                </b>
              </p>
              {singleSession.partner && (
                <p>
                  Issue category: <b>{singleSession.issueType}</b>
                </p>
              )}
            </div>
            <div className="debugging-form">
              <div className="bug-category">
                <p>
                  <b>Bug category: </b>
                </p>
                <input
                  type="bugCategory"
                  placeholder="NullPointerException"
                  value={bugCategory}
                  onChange={(e) => setBugCategory(e.target.value)}
                />
              </div>
              <div className="debugging-process">
                <p>
                  <b>Debugging process: </b>
                </p>
                <input
                  type="debuggingProcess"
                  placeholder="Strategically placed print statements to trace the bug source, then..."
                  value={debuggingProcess}
                  onChange={(e) => setDebuggingProcess(e.target.value)}
                />
              </div>
              <button className="submit-button" onClick={handleFormSubmit}>
                Submit!
              </button>
            </div>
            <div className="escalate-container">
              <b>
                Once 15 minutes in a current session have passed, you may
                escalate:
              </b>
              {renderEscalateTimerOrButton()}
            </div>
          </div>
        );
      case UserRole.HelpRequester:
        return (
          <div
            className="debugging-partner-content"
            style={{ marginBottom: 20 }}
          >
            <div className="other-partner-info">
              <p>
                Your debugging partner is:{" "}
                <b>
                  {" "}
                  {singleSession.partner
                    ? singleSession.partner.name
                    : "No one yet!"}
                </b>{" "}
              </p>
            </div>
            <div className="escalation-content">
              {escalationResult && <p>{escalationResult}</p>}
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  // creation of star background for the code
  return (
    <div className="body">
      <div id="stars-container">
        <div id="stars"></div>
        <div id="stars2"></div>
        <div id="stars3"></div>
      </div>
      <div className="dashboard-body">
        {renderHeaderBasedOnRole(userSession.role)}
        <div className="dashboard-container">
          <div className="welcome-container">
            <h1>Welcome, {userSession.user?.name.split(" ")[0]}!</h1>
            <button className="tooltip">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="tooltip-icon"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#000000"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                onClick={openResourcesWebsite}
              >
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
              </svg>
              <span className="tooltiptext">Debugging Recipe</span>
            </button>
          </div>
          {renderContentBasedOnRole(userSession.role)}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
