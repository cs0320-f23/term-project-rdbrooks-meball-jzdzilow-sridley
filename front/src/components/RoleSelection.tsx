import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/RoleSelection.css";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import { UserRole, userSessionState } from "../recoil/atoms";

// adds debugging partner to queue on backend
function addUserToQueue(email: string, name: string): Promise<string> {
  return fetch(
    "http://localhost:3333/addDebuggingPartner?name=" + name + "&email=" + email
  )
    .then((response) => response.json())
    .then((data) => {
      return data["result"];
    })
    .catch((e) => {
      return "ERROR: " + e;
    });
}

const RoleSelection = () => {
  const navigate = useNavigate();
  const [userSession, setUserSession] = useRecoilState(userSessionState);

  // if no user then go to login page
  useEffect(() => {
    if (userSession.user === null) {
      setUserSession({ user: null, role: UserRole.NoneSelected, time: null });
      navigate("/login");
    }
  }, [userSession.user]);

  // student selects type of role
  const handleRoleSelection = (role: UserRole) => {
    setUserSession({ user: userSession.user, role: role, time: new Date() });
    navigate("/dashboard");
    // will only get to role selection page if session started, thus do not need to check again
    if (role === UserRole.DebuggingPartner) {
      if (userSession.user) {
        addUserToQueue(userSession.user.email, userSession.user.name);
      }
      navigate("/dashboard");
    } else if (role === UserRole.HelpRequester) {
      // if help requester must also select type of issue (created based on user research)
      navigate("/issue-type-selection");
      console.log("ROLE" + userSession.role + "USER " + userSession.user);
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
