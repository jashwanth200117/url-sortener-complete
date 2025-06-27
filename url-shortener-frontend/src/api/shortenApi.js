// src/api/shortenApi.js

export const shortenUrl = async (originalUrl) => {
  try {
    const response = await fetch("http://localhost:8080/shorten", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        // We are not sending an authorization header here
      },
      body: JSON.stringify({ originalUrl }),
    });

    const data = await response.json();

    if (!response.ok || !data.success) {
      throw new Error(data.message || "Failed to shorten URL");
    }

    return data;  // Expected data: { success: true, shortCode: "1cd3e0", message: "Shortened successfully", ... }
  } catch (err) {
    console.error("❌ Error in shortenUrl:", err.message);
    throw err;
  }
};
