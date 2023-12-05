// Used the following video: https://www.youtube.com/watch?v=vDT7EnUpEoo

// Added to import from other services
import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";

// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCpEp6SMcO3hen7UKMeR-NNEy7ySSWStYI",
  authDomain: "collab-section-manager.firebaseapp.com",
  projectId: "collab-section-manager",
  storageBucket: "collab-section-manager.appspot.com",
  messagingSenderId: "682750943740",
  appId: "1:682750943740:web:36bb8a15ff7bea374fd279",
  measurementId: "G-1FFWDE2HTT",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);

const provider = new GoogleAuthProvider();

export const signInWithGoogle = () => {
  signInWithPopup(auth, provider)
    .then((result) => {
      console.log(result);
      const name: string | null = result.user.displayName;
      const email: string | null = result.user.email;
      console.log(name);
      console.log(email);
    })
    .catch((error) => {
      console.log(error);
    });
};
