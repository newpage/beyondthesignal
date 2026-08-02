import type { ReplayMarker } from "../types";

type Props = Readonly<{
  currentIndex: number;
  totalFrames: number;
  firstTick: number;
  lastTick: number;
  currentTick: number;
  atLiveEdge: boolean;
  markers: readonly ReplayMarker[];
  bookmarks: readonly number[];
  onSeek: (index: number) => void;
  onToggleBookmark: () => void;
  onJumpToMarker: (tick: number) => void;
}>;

const markerPosition = (
  marker: ReplayMarker,
  firstTick: number,
  lastTick: number,
): number => {
  if (lastTick <= firstTick) return 0;
  return ((marker.tick - firstTick) / (lastTick - firstTick)) * 100;
};

export const ReplayTimeline = ({
  currentIndex,
  totalFrames,
  firstTick,
  lastTick,
  currentTick,
  atLiveEdge,
  markers,
  bookmarks,
  onSeek,
  onToggleBookmark,
  onJumpToMarker,
}: Props) => {
  const maxIndex = Math.max(totalFrames - 1, 0);
  const bookmarked = bookmarks.includes(currentTick);

  return (
    <section className="replay-timeline" aria-label="Battle replay timeline">
      <header>
        <div>
          <span className="eyebrow">TIME MACHINE</span>
          <strong>{atLiveEdge ? "LIVE EDGE" : `REPLAY · TICK ${currentTick.toLocaleString()}`}</strong>
        </div>
        <button
          type="button"
          className={bookmarked ? "active" : undefined}
          onClick={onToggleBookmark}
        >
          {bookmarked ? "Remove Bookmark" : "Bookmark Tick"}
        </button>
      </header>

      <div className="timeline-track">
        <input
          aria-label="Replay position"
          type="range"
          min={0}
          max={maxIndex}
          step={1}
          value={Math.min(currentIndex, maxIndex)}
          disabled={totalFrames <= 1}
          onChange={(event) => onSeek(Number(event.target.value))}
        />
        <div className="timeline-markers" aria-hidden="true">
          {markers.map((marker) => (
            <button
              key={`${marker.tick}-${marker.type}-${marker.label}`}
              type="button"
              className={`timeline-marker ${marker.type.toLowerCase()}`}
              style={{ left: `${markerPosition(marker, firstTick, lastTick)}%` }}
              title={`${marker.label} · tick ${marker.tick}`}
              onClick={() => onJumpToMarker(marker.tick)}
            />
          ))}
          {bookmarks.map((tick) => (
            <button
              key={`bookmark-${tick}`}
              type="button"
              className="timeline-marker bookmark"
              style={{ left: `${markerPosition({ tick, type: "BOOKMARK", label: "Bookmark" }, firstTick, lastTick)}%` }}
              title={`Bookmark · tick ${tick}`}
              onClick={() => onJumpToMarker(tick)}
            />
          ))}
        </div>
      </div>

      <footer>
        <span>Tick {firstTick.toLocaleString()}</span>
        <span>{totalFrames.toLocaleString()} frames retained</span>
        <span>Tick {lastTick.toLocaleString()}</span>
      </footer>
    </section>
  );
};
