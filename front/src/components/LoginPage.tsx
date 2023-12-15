import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";
import { useRecoilState, useSetRecoilState } from "recoil";
import { mockedMode, userSessionState } from "../recoil/atoms";
import { UserRole } from "../recoil/atoms";
import { IUser } from "../types/IUser";

// Used the following video for firebase authentication: https://www.youtube.com/watch?v=vDT7EnUpEoo
import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";
// Import functions needed from appropriate SDKs
import { initializeApp } from "firebase/app";
import { firebaseConfig } from "../private/FirebaseAPI";

// using login email, determine if instructor by calling backend
function getRoleFromBackend(email: string): Promise<string> {
  return fetch("http://localhost:3333/isInstructor?email=" + email)
    .then((response) => response.json())
    .then((data) => {
      return data["message"]; // either student or instructor
    })
    .catch((error) => {
      console.log(
        "Error encountered when fetching role from backend: " + error
      );
    });
}

// checks if session started by determining if backend call to get info successful
export function checkSessionStarted(): Promise<boolean> {
  return fetch("http://localhost:3333/getInfo")
    .then((response) => response.json())
    .then((data) => {
      if (data["result"] === "success") {
        return true;
      } else {
        // error with getting info means session not successful started
        return false;
      }
    })
    .catch((error) => {
      alert("ERROR " + error);
      return false;
    });
}

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const setUserSession = useSetRecoilState(userSessionState);
  const [isMockedMode, setMockedMode] = useRecoilState(mockedMode);

  // MOCKED LOGIN - uses mocked data to determine user role and navigate to appropriate page
  const handleLoginMocked = async () => {
    const response = await fetch("http://localhost:2000/login/");
    const data = await response.json();
    const users: IUser[] = data.users;

    const user = users.find((u) => u.email === email);
    if (user) {
      if (user.role === "student") {
        setUserSession({ user: user, role: UserRole.NoneSelected, time: null });
        return navigate("/role-selection");
      } else if (user.role === "instructor") {
        setUserSession({ user: user, role: UserRole.Instructor, time: null });
        return navigate("/dashboard");
      }
    } else {
      return navigate("/failed-login");
    }
  };

  const handleMockedMode = (ifMocked: boolean) => {
    setMockedMode(ifMocked);
  };

  // initialize Firebase login and redirect to appropriate page
  const app = initializeApp(firebaseConfig);
  const auth = getAuth(app);
  const provider = new GoogleAuthProvider();

  const signInWithGoogle = async () => {
    try {
      // connects to google auth
      const result = await signInWithPopup(auth, provider);

      // retrieves email and name stored by google auth
      const name: string | null = result.user.displayName;
      const email: string | null = result.user.email;

      if (name == null || email == null) {
        return navigate("/failed-login");
      } else {
        // create IUser from email and name (role will be set later)
        let user: IUser = {
          email: email,
          name: name,
          role: "",
        };

        // // defensive programming
        // if (!user?.email.includes("brown.edu")) {
        //   return navigate("/failed-login");
        // }

        try {
          const roleFromBackend = await getRoleFromBackend(user.email);
          user.role = roleFromBackend;
        } catch (error) {
          console.error("Error fetching role from backend:", error);
          // Determine what to do with the error, handle it as needed
          return navigate("/failed-login");
        }
        if (user.role === "student") {
          // if a session has not been started, then create pop up
          checkSessionStarted()
            .then((isSessionStarted) => {
              if (!isSessionStarted) {
                return alert("No session has been started by an instructor.");
              } else {
                // setting user session provides info for rest of frontend to use
                setUserSession({
                  user: user,
                  role: UserRole.NoneSelected, // students still have to select role
                  time: null,
                });
                return navigate("/role-selection");
              }
            })
            .catch((error) => {
              console.log("Error encountered: " + error);
            });
        } else if (user.role === "instructor") {
          checkSessionStarted()
            .then((isSessionStarted) => {
              // if session already started and instructor alert that cannot join
              if (isSessionStarted) {
                return alert(
                  "Only one session can be held at a time and an instructor has already started a session."
                );
              } else {
                // setting user session provides info for rest of frontend to use
                setUserSession({
                  user: user,
                  role: UserRole.Instructor,
                  time: null,
                });
                return navigate("/dashboard");
              }
            })
            .catch((error) => {
              console.log("Error encountered: " + error);
            });
        }
      }
    } catch (error) {
      console.log("Error encountered during user authentication: " + error);
    }
  };

  // two different displays depending on if mocked mode
  if (!isMockedMode) {
    return (
      <div className="login-body">
        <div className="login-container">
          <h1>Welcome to Collab Section</h1>
          <button onClick={signInWithGoogle} className="btn">
            Sign In With Google
          </button>
        </div>
        <button
          onClick={() => handleMockedMode(true)}
          className="mocked-button"
        >
          Mocked Mode
        </button>
      </div>
    );
  } else {
    return (
      <div className="login-body">
        <div className="login-container">
          <h1>Welcome to Collab Section</h1>
          <div className="input-box">
            <input
              type="email"
              placeholder="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="input-box">
            <input
              type="password"
              placeholder="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button onClick={handleLoginMocked} className="btn">
            Login
          </button>
        </div>
        <button
          onClick={() => handleMockedMode(false)}
          className="mocked-button"
        >
          Regular Mode
        </button>
      </div>
    );
  }
};

export default LoginPage;
