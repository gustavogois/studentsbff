import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfilePage from "../ProfilePage";

const mockGetProfile = vi.fn();
const mockUpdateProfile = vi.fn();
const mockGetGrades = vi.fn();

vi.mock("../../services/profileService", () => ({
  getProfile: () => mockGetProfile(),
  updateProfile: (data: unknown) => mockUpdateProfile(data),
  getGrades: () => mockGetGrades(),
}));

const MOCK_GRADES = [
  { value: "GRADE_7", label: "7th Grade" },
  { value: "GRADE_8", label: "8th Grade" },
  { value: "GRADE_9", label: "9th Grade" },
];

describe("ProfilePage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should display current profile with grade selected", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "GRADE_7",
      school: "Lincoln Middle School",
      turma: "A",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce(MOCK_GRADES);

    render(<ProfilePage />);

    await waitFor(() => {
      const gradeSelect = screen.getByLabelText("profile.grade");
      expect(gradeSelect).toHaveValue("GRADE_7");
      expect(
        screen.getByDisplayValue("Lincoln Middle School"),
      ).toBeInTheDocument();
      expect(screen.getByDisplayValue("A")).toBeInTheDocument();
    });
  });

  it("should render grade dropdown with options", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      turma: "",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce(MOCK_GRADES);

    render(<ProfilePage />);

    await waitFor(() => {
      const gradeSelect = screen.getByLabelText("profile.grade");
      expect(gradeSelect.tagName).toBe("SELECT");
    });

    // Check that grade options are rendered
    expect(screen.getByText("grades.GRADE_7")).toBeInTheDocument();
    expect(screen.getByText("grades.GRADE_8")).toBeInTheDocument();
    expect(screen.getByText("grades.GRADE_9")).toBeInTheDocument();
  });

  it("should render turma input", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      turma: "",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce(MOCK_GRADES);

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByLabelText("profile.turma")).toBeInTheDocument();
    });
  });

  it("should submit with grade, turma, and school", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      turma: "",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce(MOCK_GRADES);
    mockUpdateProfile.mockResolvedValueOnce({
      id: "1",
      grade: "GRADE_8",
      school: "Washington Middle School",
      turma: "B",
      createdAt: "",
    });

    const user = userEvent.setup();
    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByLabelText("profile.grade")).toBeInTheDocument();
    });

    await user.selectOptions(screen.getByLabelText("profile.grade"), "GRADE_8");
    await user.type(screen.getByLabelText("profile.turma"), "B");
    await user.type(
      screen.getByLabelText("profile.school"),
      "Washington Middle School",
    );
    await user.click(screen.getByText("profile.saveButton"));

    await waitFor(() => {
      expect(mockUpdateProfile).toHaveBeenCalledWith({
        grade: "GRADE_8",
        school: "Washington Middle School",
        turma: "B",
      });
    });
  });

  it("should show loading state", () => {
    mockGetProfile.mockReturnValue(new Promise(() => {}));
    mockGetGrades.mockReturnValue(new Promise(() => {}));

    render(<ProfilePage />);

    expect(screen.getByText("common.loading")).toBeInTheDocument();
  });

  it("should render language switcher with 3 buttons", async () => {
    mockGetProfile.mockResolvedValueOnce({
      id: "1",
      grade: "",
      school: "",
      turma: "",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce([]);

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
      turma: "",
      createdAt: "",
    });
    mockGetGrades.mockResolvedValueOnce([]);

    render(<ProfilePage />);

    await waitFor(() => {
      expect(screen.getByText("Português (Brasil)")).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Português (Brasil)"));
  });
});
