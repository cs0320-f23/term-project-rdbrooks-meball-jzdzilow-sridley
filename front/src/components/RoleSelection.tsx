import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/RoleSelection.css";
import { useRecoilState } from "recoil";
import { UserRole, userSessionState } from "../recoil/atoms";

const RoleSelection = () => {
  const navigate = useNavigate();
  const [userSession, setUserSession] = useRecoilState(userSessionState);

  useEffect(() => {
    if (userSession.user === null) {
      setUserSession({ user: null, role: UserRole.NoneSelected, time: null });
      navigate("/login");
    }
  }, [userSession.user]);

  const handleRoleSelection = (role: UserRole) => {
    setUserSession({
      user: userSession.user,
      role: role,
      time: new Date(),
    });
    if (role === UserRole.DebuggingPartner) {
      navigate("/dashboard");
    } else if (role === UserRole.HelpRequester) {
      navigate("/issue-type-selection");
    }
  };

  return (
    <div className="role-body">
      <div className="role-container">
        <h2>Welcome, {userSession.user?.name}!</h2>
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
        <button
          className="btn2"
          onClick={() => {
            setUserSession({
              user: null,
              role: UserRole.NoneSelected,
              time: null,
            });
            navigate("/login");
          }}
        >
          Back to Login
        </button>
      </div>
    </div>
  );
};

export default RoleSelection;
