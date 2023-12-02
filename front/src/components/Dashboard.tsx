import React, { useEffect, useState } from "react";
import { useRecoilState, useSetRecoilState } from "recoil";
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

  /* -------------------------------timer content ---------------------------------------*/

  function calculateFullTimeRemaining() {
    if (userSession.time === null) {
      /* ------------- CHECK --------------- */
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

  const openResourcesWebsite = () => {
    const url: string = "https://hackmd.io/@brown-csci0320/BJKCtyxxs/";
    window.open(url, "_blank");
  };

  /* ---------------------------------- handlers -----------------------------------------*/

  const handleSubmit = async () => {
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

  /* ----------------------- end of handlers / below rendering ---------------------------*/

  const renderHeaderBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return <p>instructor content here.</p>;
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

  const renderContentBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return <p>instructor content here.</p>;
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
              <button className="submit-button" onClick={handleSubmit}>
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
