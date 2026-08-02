export type RenderCallback<T> = (frame: T, timestamp: number) => void;

export class RenderScheduler<T> {
  private queued?: T;
  private handle?: number;

  public constructor(private readonly render: RenderCallback<T>) {}

  public enqueue(frame: T): void {
    this.queued = frame;
    if (this.handle !== undefined) {
      return;
    }
    this.handle = window.requestAnimationFrame((timestamp) => {
      this.handle = undefined;
      const next = this.queued;
      this.queued = undefined;
      if (next !== undefined) {
        this.render(next, timestamp);
      }
    });
  }

  public cancel(): void {
    if (this.handle !== undefined) {
      window.cancelAnimationFrame(this.handle);
    }
    this.handle = undefined;
    this.queued = undefined;
  }
}
