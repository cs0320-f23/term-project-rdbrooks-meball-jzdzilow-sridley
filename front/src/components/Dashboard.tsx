import React, { useEffect, useState } from "react";
import { useRecoilValue } from "recoil";
import { UserRole, userRoleState, userState } from "../recoil/atoms";
import "../styles/Dashboard.css";
import { useNavigate } from "react-router-dom";
import Timer from "./Timer";
import "../styles/nightsky.scss";

const Dashboard = () => {
  const user = useRecoilValue(userState);
  const userRole = useRecoilValue(userRoleState);
  const navigate = useNavigate();
  const [partner, setPartner] = useState<string | null>(null);
  const [issue, setIssue] = useState<string | null>(null);
  const [bugCategory, setBugCategory] = useState("");
  const [debuggingProcess, setDebuggingProcess] = useState("");

  useEffect(() => {
    if (user === null) {
      navigate("/login");
    }
  }, [user]);

  useEffect(() => {
    const fetchPartner = async () => {
      try {
        const response = await fetch("http://localhost:2000/getPartner");
        const data = await response.json();
        const partner = data.partner;
        const issue = data.issue;
        setPartner(partner);
        setIssue(issue);
      } catch (error) {
        console.error("Error fetching partner:", error);
      }
    };

    if (userRole.role === UserRole.DebuggingPartner) {
      fetchPartner();
    }
  }, [userRole.role]);

  const openResourcesWebsite = () => {
    const url: string = "https://hackmd.io/@brown-csci0320/BJKCtyxxs/";
    window.open(url, "_blank");
  };

  const handleSubmit = async () => {
    if (user && partner) {
      console.log(user.email, partner, bugCategory, debuggingProcess);
      try {
        const response = await fetch("http://localhost:2000/submitForm/", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            data: {
              user: user.email,
              partner: partner,
              bugCategory: bugCategory,
              debuggingProcess: debuggingProcess,
            },
          }),
        });

        if (!response.ok) {
          // If the server returns an error status
          throw new Error(`Server error: ${response.status}`);
        } else {
          const result = await response.json();
          if (result.success) {
            setPartner(null);
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

  const renderHeaderBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return <p>instructor content here.</p>;
      case UserRole.DebuggingPartner:
        return (
          <header className="user-header">
            <p className="join-time">
              Join time: {userRole?.time?.toLocaleTimeString()}
            </p>
            <Timer joinTime={userRole?.time?.getTime()} />
          </header>
        );
      case UserRole.HelpRequester:
        return <p>help requester content here.</p>;
      default:
        return null;
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
                <b> {partner ? partner : "No one yet!"}</b>
              </p>
              {partner && (
                <p>
                  Issue category: <b>{issue}</b>
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
        return <p>help requester content here.</p>;
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
        {renderHeaderBasedOnRole(userRole.role)}
        <div className="dashboard-container">
          <div className="welcome-container">
            <h1>Welcome, {user?.name.split(" ")[0]}!</h1>
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
          {renderContentBasedOnRole(userRole.role)}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
