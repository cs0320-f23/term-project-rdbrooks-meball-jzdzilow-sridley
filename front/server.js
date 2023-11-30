import express from "express";
import { join } from "path"; // Import the path module
import { fileURLToPath } from "url";
import { dirname } from "path";
import cors from "cors";
import fs from "fs";

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

app.get("/getPartner", (req, res) => {
  const filePath = join(__dirname, "/public/mockPartner.json");
  res.sendFile(filePath);
});

app.post("/submitForm", express.json(), (req, res) => {
  const { data } = req.body;
  if (!data) {
    return res.status(400).json({ error: "Data is required." });
  }
  // Convert data to CSV format (assuming data is an array of objects)
  const csvContent = data
    .map((item) => Object.values(item).join(","))
    .join("\n");

  const csvFilePath = join(__dirname, "/public/formData.csv");
  fs.writeFileSync(csvFilePath, csvContent);

  res.json({ success: true });
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
