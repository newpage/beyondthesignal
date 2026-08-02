import type { BattleFrame, BattleFrameV1 } from "../types";
import { adaptBattleFrame } from "./battleFrameAdapter";

export type FrameHandler = (frame: BattleFrame) => void;
export type StatusHandler = (
  status:
    | "CONNECTING"
    | "CONNECTED"
    | "RECONNECTING"
    | "DISCONNECTED",
) => void;

export class BattleTelemetryClient {
  private socket?: WebSocket;
  private reconnectTimer?: number;
  private reconnectAttempt = 0;
  private stopped = false;

  public constructor(
    private readonly url: string,
    private readonly onFrame: FrameHandler,
    private readonly onStatus: StatusHandler,
  ) {}

  public start(): void {
    this.stopped = false;
    this.connect();
  }

  public stop(): void {
    this.stopped = true;

    if (this.reconnectTimer !== undefined) {
      window.clearTimeout(this.reconnectTimer);
    }

    this.socket?.close();
    this.socket = undefined;
    this.onStatus("DISCONNECTED");
  }

  private connect(): void {
    if (this.stopped) {
      return;
    }

    this.onStatus(
      this.reconnectAttempt === 0 ? "CONNECTING" : "RECONNECTING",
    );

    const socket = new WebSocket(this.url);
    this.socket = socket;

    socket.onopen = () => {
      this.reconnectAttempt = 0;
      this.onStatus("CONNECTED");
    };

    socket.onmessage = (event) => {
      try {
        const frame = JSON.parse(String(event.data)) as BattleFrameV1;
        this.onFrame(adaptBattleFrame(frame));
      } catch (error) {
        console.error("Invalid battle telemetry frame", error);
      }
    };

    socket.onerror = () => socket.close();

    socket.onclose = () => {
      if (this.stopped) {
        return;
      }

      this.reconnectAttempt += 1;
      this.onStatus("RECONNECTING");

      const delay = Math.min(
        10_000,
        500 * 2 ** Math.min(this.reconnectAttempt, 5),
      );

      this.reconnectTimer = window.setTimeout(
        () => this.connect(),
        delay,
      );
    };
  }
}
