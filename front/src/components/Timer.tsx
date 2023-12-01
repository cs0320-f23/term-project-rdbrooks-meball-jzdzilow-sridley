const Timer = ({ timeRemaining }: { timeRemaining: number }) => {
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
