import React, { useEffect, useState } from "react";
import { useRecoilValue } from "recoil";
import { UserRole, userRoleState, userState } from "../recoil/atoms";
import "../styles/Dashboard.css";
import { useNavigate } from "react-router-dom";
import Timer from "./Timer";

const Dashboard = () => {
  const user = useRecoilValue(userState);
  const userRole = useRecoilValue(userRoleState);
  const navigate = useNavigate();

  useEffect(() => {
    if (user === null) {
      navigate("/login");
    }
  }, [userState, userRoleState, user]);

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
        return <p>debugging partner content here.</p>;
      case UserRole.HelpRequester:
        return <p>help requester content here.</p>;
      default:
        return null;
    }
  };

  return (
    <div className="dashboard-body">
      {renderHeaderBasedOnRole(userRole.role)}
      <div className="dashboard-container">
        <div>
          <h1>Welcome, {user?.name.split(" ")[0]}!</h1>
        </div>
        {renderContentBasedOnRole(userRole.role)}
      </div>
    </div>
  );
};

export default Dashboard;
