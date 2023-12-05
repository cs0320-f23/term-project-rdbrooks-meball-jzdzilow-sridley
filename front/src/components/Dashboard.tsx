import React, { useEffect, useState } from "react";
import { useRecoilValue, useRecoilState } from "recoil";
import { IssueType, UserRole, userRoleState, userState, singleSessionState, userSessionState } from "../recoil/atoms";
import "../styles/Dashboard.css";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const [userSession, setUserSession] = useRecoilState(userSessionState);
  const [singleSession, setSingleSession] = useRecoilState(singleSessionState);
  const navigate = useNavigate();
  const [bugCategory, setBugCategory] = useState("");
  const user = useRecoilValue(userState);
  const userRole = useRecoilValue(userRoleState);

  // useEffect(() => {
  //   if (user === null) {
  //     navigate("/login");
  //   }
  // }, [userState, userRoleState, user]);

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

  const renderContentBasedOnRole = (role: UserRole) => {
    switch (role) {
      case UserRole.Instructor:
        return <p>instructor content here.</p>;
      case UserRole.DebuggingPartner:
        return <p>debugging partner content here.</p>;
      case UserRole.HelpRequester:
        return <p>help requester content here.</p>;
      default:
        return null;
    }
  };

  return (
    <div className="dashboard-body">
      <header className="join-time">
        join time: {userRole?.time?.toLocaleTimeString()}
      </header>
      <div className="dashboard-container">
        <div>
          <h1>Welcome, {userSession.user?.name.split(" ")[0]}!</h1>
        </div>
        {renderContentBasedOnRole(userRole.role)}
      </div>
    </div>
  );
};

export default Dashboard;
