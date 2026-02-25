import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Calendar from "../Calendar";
import type { SchoolEvent } from "../../types";

const mockEvents: SchoolEvent[] = [
  {
    id: "1",
    title: "Math Exam",
    eventType: "EXAM",
    eventDate: "2026-03-15",
    subjectId: null,
    subjectName: null,
    description: null,
    createdAt: "",
    updatedAt: "",
  },
  {
    id: "2",
    title: "Essay Due",
    eventType: "ASSIGNMENT",
    eventDate: "2026-03-20",
    subjectId: null,
    subjectName: null,
    description: null,
    createdAt: "",
    updatedAt: "",
  },
];

describe("Calendar", () => {
  it("should render current month grid", () => {
    const now = new Date(2026, 2, 10); // March 2026
    vi.setSystemTime(now);

    render(<Calendar year={2026} month={2} events={[]} />);

    // March has 31 days
    expect(screen.getByText("1")).toBeInTheDocument();
    expect(screen.getByText("15")).toBeInTheDocument();
    expect(screen.getByText("31")).toBeInTheDocument();

    vi.useRealTimers();
  });

  it("should highlight today", () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date(2026, 2, 10));

    render(<Calendar year={2026} month={2} events={[]} />);

    const todayCell = screen.getByText("10").closest("[data-today]");
    expect(todayCell).toHaveAttribute("data-today", "true");

    vi.useRealTimers();
  });

  it("should navigate to next month", async () => {
    const onMonthChange = vi.fn();
    const user = userEvent.setup();

    render(
      <Calendar
        year={2026}
        month={2}
        events={[]}
        onMonthChange={onMonthChange}
      />,
    );

    await user.click(screen.getByLabelText("next-month"));

    expect(onMonthChange).toHaveBeenCalledWith(2026, 3);
  });

  it("should navigate to previous month", async () => {
    const onMonthChange = vi.fn();
    const user = userEvent.setup();

    render(
      <Calendar
        year={2026}
        month={2}
        events={[]}
        onMonthChange={onMonthChange}
      />,
    );

    await user.click(screen.getByLabelText("prev-month"));

    expect(onMonthChange).toHaveBeenCalledWith(2026, 1);
  });

  it("should render events on correct days", () => {
    render(<Calendar year={2026} month={2} events={mockEvents} />);

    // Events should show as dots/badges on their dates
    expect(screen.getByText("Math Exam")).toBeInTheDocument();
    expect(screen.getByText("Essay Due")).toBeInTheDocument();
  });

  it("should call onEventClick when event clicked", async () => {
    const onEventClick = vi.fn();
    const user = userEvent.setup();

    render(
      <Calendar
        year={2026}
        month={2}
        events={mockEvents}
        onEventClick={onEventClick}
      />,
    );

    await user.click(screen.getByText("Math Exam"));

    expect(onEventClick).toHaveBeenCalledWith(mockEvents[0]);
  });
});
