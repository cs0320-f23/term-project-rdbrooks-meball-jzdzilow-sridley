import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/IssueTypeSelection.css";
import { useRecoilState, useSetRecoilState } from "recoil";
import {
  IssueType,
  userSessionState,
  singleSessionState,
} from "../recoil/atoms";

const IssueTypeSelection = () => {
  const navigate = useNavigate();
  const [userSession, setUserSession] = useRecoilState(userSessionState);
  const setSingleSessionState = useSetRecoilState(singleSessionState);

  useEffect(() => {
    if (userSession.user === null) {
      setSingleSessionState({
        partner: null,
        issueType: IssueType.NoneSelected,
      });
      navigate("/login");
    }
  }, [userSession.user]);

  const handleIssueSelection = (issueType: IssueType) => {
    setSingleSessionState({ partner: null, issueType: issueType });
    navigate("/dashboard");
    // Do something based on the selected role (e.g., navigate to a specific page)
  };

  return (
    <div className="issue-body">
      <div className="issue-container">
        <h2>
          {userSession.user?.name.split(" ")[0]}, what do you need help with?
        </h2>
        <button
          onClick={() => handleIssueSelection(IssueType.Bug)}
          className="btn"
        >
          A bug
        </button>
        <button
          onClick={() => handleIssueSelection(IssueType.ConceptualQuestion)}
          className="btn"
        >
          A conceptual question
        </button>
      </div>
    </div>
  );
};

export default IssueTypeSelection;
