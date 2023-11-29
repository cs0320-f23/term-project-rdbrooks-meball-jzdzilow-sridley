import express from "express";
import { join } from "path"; // Import the path module
import { fileURLToPath } from "url";
import { dirname } from "path";
import cors from "cors";

//create local server to host files in project directory
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express(); //express helps set up http server
app.use(cors()); //cors allows us to access resources from remote host
const port = 2000;

// Serve login JSON file
app.get("/login", (req, res) => {
  const filePath = join(__dirname, "/public/mockLogin.json");
  res.sendFile(filePath);
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
