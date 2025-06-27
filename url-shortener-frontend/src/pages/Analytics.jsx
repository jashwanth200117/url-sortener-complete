import React, { useEffect, useState } from 'react';

const Analytics = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  const shortCode = "596489"; // You can make this dynamic later
  const page = 0;
  const size = 5;

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const res = await fetch(`http://localhost:8080/analytics/${shortCode}?page=${page}&size=${size}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnYWgiLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzUwOTE2MDY1LCJleHAiOjE3NTEwMDI0NjV9.sxIUGyZDRHqW4q1SPQ6HC8JnsjbKmSJV6uJ3CZJjzQ0`
          },
        });

        if (!res.ok) {
          throw new Error(`Failed with status ${res.status}`);
        }

        const json = await res.json();
        setData(json);
      } catch (err) {
        console.error("Failed to fetch analytics:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  return (
    <div className="container mt-5">
      <div className="text-center mb-4">
        <h2>📊 URL Analytics</h2>
      </div>

      {loading ? (
        <div className="text-center">
          <div className="spinner-border text-primary" role="status" />
        </div>
      ) : !data || data.clicks.length === 0 ? (
        <div className="alert alert-info text-center">No analytics data available.</div>
      ) : (
        <>
          <div className="mb-3 text-end">
            <strong>Total Clicks:</strong> {data.totalItems} | <strong>Page:</strong> {data.currentPage + 1} of {data.totalPages}
          </div>

          <div className="table-responsive">
            <table className="table table-bordered table-hover align-middle">
              <thead className="table-light">
                <tr>
                  <th>Short Code</th>
                  <th>Original URL</th>
                  <th>IP Address</th>
                  <th>User Agent</th>
                  <th>Accessed At</th>
                </tr>
              </thead>
              <tbody>
                {data.clicks.map((entry, index) => (
                  <tr key={index}>
                    <td>{entry.shortCode}</td>
                    <td>
                      <a href={entry.originalUrl} target="_blank" rel="noopener noreferrer">
                        {entry.originalUrl}
                      </a>
                    </td>
                    <td>{entry.ipAddress}</td>
                    <td className="text-break">{entry.userAgent}</td>
                    <td>{new Date(entry.accessedAt.split('[')[0]).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
};

export default Analytics;
