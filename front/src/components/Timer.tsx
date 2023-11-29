import React, { useEffect, useState } from "react";

const Timer = ({ joinTime }: { joinTime?: number }) => {
  const [timeRemaining, setTimeRemaining] = useState(calculateTimeRemaining());

  function calculateTimeRemaining() {
    if (joinTime === undefined) {
      return 0;
    }

    const oneHourInMillis = 60 * 60 * 1000; // 1 hour in milliseconds
    const currentTime = new Date().getTime();
    const elapsedTime = currentTime - joinTime;
    const remainingTime = Math.max(oneHourInMillis - elapsedTime, 0);
    return remainingTime;
  }

  useEffect(() => {
    const timerInterval = setInterval(() => {
      setTimeRemaining(calculateTimeRemaining());
    }, 1000);

    return () => clearInterval(timerInterval);
  }, [joinTime]);

  const formatTime = (milliseconds: number) => {
    const minutes = Math.floor(milliseconds / (60 * 1000));
    const seconds = Math.floor((milliseconds % (60 * 1000)) / 1000);
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  };

  return (
    <div className="pending-container">
      <p className="pending">Pending: {formatTime(timeRemaining)}</p>
    </div>
  );
};

export default Timer;
