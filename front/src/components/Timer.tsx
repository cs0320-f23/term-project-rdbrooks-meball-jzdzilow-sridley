// timer used to display time remaining to students
const Timer = ({
  fullTimeRemaining: fullTimeRemaining,
}: {
  fullTimeRemaining: number;
}) => {
  const formatTime = (milliseconds: number) => {
    const minutes = Math.floor(milliseconds / (60 * 1000));
    const seconds = Math.floor((milliseconds % (60 * 1000)) / 1000);
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  };

  return (
    <div className="pending-container">
      <p className="pending">Pending: {formatTime(fullTimeRemaining)}</p>
    </div>
  );
};

export default Timer;
