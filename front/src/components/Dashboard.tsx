import React, { useEffect, useState } from "react";
import { useRecoilValue, useRecoilState, useSetRecoilState } from "recoil";
import {
  IssueType,
  UserRole,
  singleSessionState,
  userSessionState,
} from "../recoil/atoms";
import "../styles/Dashboard.css";
import { useNavigate } from "react-router-dom";
import Timer from "./Timer";
import "../styles/nightsky.scss";

const Dashboard = () => {
  const [userSession, setUserSession] = useRecoilState(userSessionState);
  const [singleSession, setSingleSession] = useRecoilState(singleSessionState);
  const navigate = useNavigate();
  const [bugCategory, setBugCategory] = useState("");
  const [debuggingProcess, setDebuggingProcess] = useState("");
  const [fullTimeRemaining, setFullTimeRemaining] = useState(
    calculateFullTimeRemaining()
  );
  const [sessionStarted, setSessionStarted] = useState(false);

  // call to get info from backend will continuously update this information
  const [unpairedDP, setUnpairedDP] = useState([]);
  const [unpairedHR, setUnpairedHR] = useState([]);
  const [escalatedPairs, setEscalatedPairs] = useState([]);
  const [nonEscalatedPairs, setNonEscalatedPairs] = useState([]);

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
              if (data["result"] === "success") {
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
          console.error("Error fetching data:", error);
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
    console.log("test");
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
        console.error("Error fetching partner:", error);
      }
    };
    fetchPartner();
    console.log(
      "issue " + singleSession.issueType,
      "partner " + singleSession.partner
    );
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

  /* ---------------------------- end of timer content ------------------------------------*/

  /* -------------------------- get info for instructors ----------------------------------*/

  /* gets information about debugging parters, help requesters and pairings from 
  backend to update instructor page */
  useEffect(() => {
    const fetchData = async () => {
      try {
        const getInfoResponse = await fetch("http://localhost:3333/getInfo")
          .then((response) => response.json())
          .then((data) => {
            // if successfully can get info (and thus session is running), set values appropriately
            if (data["result"] === "success") {
              console.log(data);

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
        console.error("Error fetching data:", error);
      }
    };

    // fetches data initally
    fetchData();

    // Fetch data every 5 seconds (adjust the interval as needed)
    const intervalId = setInterval(fetchData, 5000);
    return () => clearInterval(intervalId);
  }, []);

  /* -------------------------- end of get info for instructors ---------------------------*/

  const openResourcesWebsite = () => {
    const url: string = "https://hackmd.io/@brown-csci0320/BJKCtyxxs/";
    window.open(url, "_blank");
  };

  /* ---------------------------------- handlers -----------------------------------------*/

  const handleFormSubmit = async () => {
    if (bugCategory === "" || debuggingProcess === "") {
      return alert("Bug category and debugging process inputs required!");
    }
    if (userSession.user && singleSession.partner) {
      console.log(
        userSession.user.email,
        singleSession.partner,
        bugCategory,
        debuggingProcess
      );
      try {
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
      } catch (error) {
        alert("Error encountered: " + error);
      }
    }
  };

  const handleEscalate = () => {
    console.log("escalated");
  };

  const handleEndSession = () => {
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
      console.error("ERROR: " + error);
      // TODO: Handle errors as needed
    }
    setSessionStarted(true);
  };

  // ends the session by calling backend
  const handleEnd = async () => {
    try {
      await fetch("http://localhost:3333/session?command=end");
    } catch (error) {
      console.error("ERROR: " + error);
      // TODO: Handle errors as needed
    }
    setSessionStarted(false);
  }; // TODO: bring everybody back to login page when session ended

  // removes a debugging partner from attendance (button accessible to instructors)
  const handleRemove = (name: string, email: string) => async () => {
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
      .catch((e) => {
        return "ERROR: " + e;
        // TODO: Handle errors as needed
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
        .catch((e) => {
          return "ERROR: " + e;
          // TODO: Handle errors as needed
        });
    };

  console.log("session started" + sessionStarted);
  /* ----------------------- end of handlers / below rendering ---------------------------*/

  const renderHeaderBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return (
          <header className="instructor-header">
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
            <button className="done-button" onClick={handleEndSession}>
              I'm done!
            </button>
          </header>
        );
      default:
        return null;
    }
  };

  const renderTimerOrButton = () => {
    if (singleSession.partner && fullTimeRemaining > 0) {
      return <Timer fullTimeRemaining={fullTimeRemaining} />;
    } else if (fullTimeRemaining === 0) {
      return (
        <button className="done-button" onClick={handleEndSession}>
          I'm done!
        </button>
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
                  <button onClick={() => handleRemove(partner[0], partner[1])}>
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
              <button className="escalate-button" onClick={handleEscalate}>
                Escalate!
              </button>
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
