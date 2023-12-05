import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/IssueTypeSelection.css";
import { useRecoilState, useSetRecoilState, useRecoilValue } from "recoil";
import {
  IssueType,
  userSessionState,
  singleSessionState,
} from "../recoil/atoms";

function addUserToQueue(
  email: string,
  name: string,
  issueType: string
): Promise<string> {
  return fetch(
    "http://localhost:3333/addHelpRequester?name=" +
      name +
      "&email=" +
      email +
      "&bugType=" +
      issueType
  )
    .then((response) => response.json())
    .then((data) => {
      return data["result"];
    })
    .catch((e) => {
      return "ERROR: " + e;
    });
}

const IssueTypeSelection = () => {
  const navigate = useNavigate();
  //const user = useRecoilValue(userState);
  const [userSession, setUserSession] = useRecoilState(userSessionState);
  const setSingleSessionState = useSetRecoilState(singleSessionState);

  useEffect(() => {
    if (userSession.user === null) {
      console.log("user session null");
      setSingleSessionState({
        partner: null,
        issueType: IssueType.NoneSelected,
      });
      navigate("/login");
    }
  }, [userSession.user]);

  const handleIssueSelection = async (issueType: IssueType) => {
    setSingleSessionState({ partner: null, issueType: issueType });
    console.log("set single session state");
    console.log(issueType);
    if (userSession.user) {
      await addUserToQueue(
        userSession.user.email,
        userSession.user.name,
        issueType
      );
    }
    console.log("about to navigate to dashboard");
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
