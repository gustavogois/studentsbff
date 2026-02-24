import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import EmptyState from "../EmptyState";

describe("EmptyState", () => {
  it("should render message and icon", () => {
    render(
      <EmptyState
        icon={<span data-testid="icon">📚</span>}
        message="No items yet"
      />,
    );

    expect(screen.getByTestId("icon")).toBeInTheDocument();
    expect(screen.getByText("No items yet")).toBeInTheDocument();
  });

  it("should render CTA button when provided", () => {
    render(
      <EmptyState
        icon={<span>📚</span>}
        message="No items yet"
        actionLabel="Add Item"
        onAction={vi.fn()}
      />,
    );

    expect(screen.getByText("Add Item")).toBeInTheDocument();
  });

  it("should not render CTA when omitted", () => {
    render(<EmptyState icon={<span>📚</span>} message="No items yet" />);

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("should call onAction when CTA clicked", async () => {
    const onAction = vi.fn();
    render(
      <EmptyState
        icon={<span>📚</span>}
        message="No items yet"
        actionLabel="Add Item"
        onAction={onAction}
      />,
    );

    await userEvent.click(screen.getByText("Add Item"));

    expect(onAction).toHaveBeenCalledOnce();
  });
});
