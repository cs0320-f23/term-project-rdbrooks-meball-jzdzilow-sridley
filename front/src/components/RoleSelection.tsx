import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/RoleSelection.css";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { userRoleState, userState } from "../recoil/atoms";
import { UserRole } from "../recoil/atoms";
import { IUser } from "../types/IUser";

const RoleSelection = () => {
  const navigate = useNavigate();
  const user = useRecoilValue(userState);
  const setUserRole = useSetRecoilState(userRoleState);

  useEffect(() => {
    if (user === null) {
      navigate("/login");
    }
  }, [user]);

  const handleRoleSelection = (role: UserRole) => {
    setUserRole({ role: role, time: new Date() });
    if (role === UserRole.DebuggingPartner) {
      navigate("/dashboard");
    } else if (role === UserRole.HelpRequester) {
      navigate("/issue-type-selection");
    }
  };

  return (
    <div className="role-body">
      <div className="role-container">
        <h2>Welcome, {user?.name}!</h2>
        <p>Please select your role:</p>
        <button
          onClick={() => handleRoleSelection(UserRole.HelpRequester)}
          className="btn"
        >
          Help Requester
        </button>
        <button
          onClick={() => handleRoleSelection(UserRole.DebuggingPartner)}
          className="btn"
        >
          Debugging Partner
        </button>
        <button className="btn2" onClick={() => navigate("/login")}>
          Back to Login
        </button>
      </div>
    </div>
  );
};

export default RoleSelection;
