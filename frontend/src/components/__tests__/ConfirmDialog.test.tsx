import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ConfirmDialog from "../ConfirmDialog";

describe("ConfirmDialog", () => {
  it("should render title and message when open", () => {
    render(
      <ConfirmDialog
        open={true}
        title="Delete Item"
        message="Are you sure?"
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
      />,
    );

    expect(screen.getByText("Delete Item")).toBeInTheDocument();
    expect(screen.getByText("Are you sure?")).toBeInTheDocument();
  });

  it("should not render when closed", () => {
    render(
      <ConfirmDialog
        open={false}
        title="Delete Item"
        message="Are you sure?"
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
      />,
    );

    expect(screen.queryByText("Delete Item")).not.toBeInTheDocument();
  });

  it("should call onConfirm when confirm button clicked", async () => {
    const onConfirm = vi.fn();
    render(
      <ConfirmDialog
        open={true}
        title="Delete Item"
        message="Are you sure?"
        onConfirm={onConfirm}
        onCancel={vi.fn()}
      />,
    );

    await userEvent.click(screen.getByText("confirm.delete"));

    expect(onConfirm).toHaveBeenCalledOnce();
  });

  it("should call onCancel when cancel button clicked", async () => {
    const onCancel = vi.fn();
    render(
      <ConfirmDialog
        open={true}
        title="Delete Item"
        message="Are you sure?"
        onConfirm={vi.fn()}
        onCancel={onCancel}
      />,
    );

    await userEvent.click(screen.getByText("confirm.cancel"));

    expect(onCancel).toHaveBeenCalledOnce();
  });
});
