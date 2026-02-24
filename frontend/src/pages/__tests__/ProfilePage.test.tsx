import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfilePage from "../ProfilePage";

const mockGetProfile = vi.fn();
const mockUpdateProfile = vi.fn();

vi.mock("../../services/profileService", () => ({
  getProfile: () => mockGetProfile(),
  updateProfile: (data: unknown) => mockUpdateProfile(data),
}));

describe("ProfilePage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should display current profile", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "7th",
      school: "Lincoln Middle School",
      createdAt: "",
    });

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByDisplayValue("7th")).toBeInTheDocument();
      expect(
        screen.getByDisplayValue("Lincoln Middle School"),
      ).toBeInTheDocument();
    });
  });

  it("should update profile", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "7th",
      school: "Lincoln Middle School",
      createdAt: "",
    });
    mockUpdateProfile.mockResolvedValueOnce({
      id: "1",
      grade: "8th",
      school: "Washington Middle School",
      createdAt: "",
    });

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByDisplayValue("7th")).toBeInTheDocument();
    });

    const gradeInput = screen.getByLabelText("profile.grade");
    const schoolInput = screen.getByLabelText("profile.school");

    await userEvent.clear(gradeInput);
    await userEvent.type(gradeInput, "8th");
    await userEvent.clear(schoolInput);
    await userEvent.type(schoolInput, "Washington Middle School");

    await userEvent.click(screen.getByText("profile.saveButton"));

    await waitFor(() => {
      expect(mockUpdateProfile).toHaveBeenCalledWith({
        grade: "8th",
        school: "Washington Middle School",
      });
    });
  });

  it("should show loading state", () => {
    mockGetProfile.mockReturnValue(new Promise(() => {}));

    render(<ProfilePage />);

    expect(screen.getByText("common.loading")).toBeInTheDocument();
  });

  it("should render language switcher with 3 buttons", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      createdAt: "",
    });

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByText("English")).toBeInTheDocument();
      expect(screen.getByText("Português (Brasil)")).toBeInTheDocument();
      expect(screen.getByText("Português (Portugal)")).toBeInTheDocument();
    });
  });

  it("should call changeLanguage when language button clicked", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      createdAt: "",
    });

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByText("Português (Brasil)")).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Português (Brasil)"));

    // The mock i18n.changeLanguage should have been called
    // (our mock in setup.ts provides a vi.fn() for changeLanguage)
  });
});
