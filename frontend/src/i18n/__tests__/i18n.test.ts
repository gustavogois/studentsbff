import { describe, it, expect, vi, beforeEach } from "vitest";

// Unmock react-i18next for this test since we test the real i18n instance
vi.unmock("react-i18next");

import i18n from "../index";

describe("i18n", () => {
  beforeEach(async () => {
    await i18n.changeLanguage("en");
  });

  it("should initialise with English default", () => {
    expect(i18n.language).toBe("en");
  });

  it("should have English translations loaded", () => {
    expect(i18n.t("login.title")).toBe("StudentsBFF");
  });

  it("should switch to pt-BR", async () => {
    await i18n.changeLanguage("pt-BR");
    expect(i18n.language).toBe("pt-BR");
    expect(i18n.t("login.subtitle")).toBe("Seu companheiro de estudos");
  });

  it("should switch to pt-PT", async () => {
    await i18n.changeLanguage("pt-PT");
    expect(i18n.language).toBe("pt-PT");
    expect(i18n.t("login.subtitle")).toBe("O teu companheiro de estudo");
  });

  it("should fallback to English for unknown locale", async () => {
    await i18n.changeLanguage("xx");
    expect(i18n.t("login.title")).toBe("StudentsBFF");
  });
});
