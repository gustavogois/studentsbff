import { useTranslation } from "react-i18next";

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  confirmLabel?: string;
  variant?: "danger" | "default";
}

export default function ConfirmDialog({
  open,
  title,
  message,
  onConfirm,
  onCancel,
  confirmLabel,
  variant = "danger",
}: ConfirmDialogProps) {
  const { t } = useTranslation();

  if (!open) return null;

  const confirmButtonClass =
    variant === "danger"
      ? "bg-red-600 hover:bg-red-700 text-white"
      : "bg-indigo-600 hover:bg-indigo-700 text-white";

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      role="dialog"
      aria-modal="true"
    >
      <div className="mx-4 w-full max-w-sm rounded-lg bg-white p-6 shadow-xl">
        <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
        <p className="mt-2 text-sm text-gray-600">{message}</p>
        <div className="mt-4 flex justify-end gap-3">
          <button
            onClick={onCancel}
            className="rounded-md bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200"
          >
            {t("confirm.cancel")}
          </button>
          <button
            onClick={onConfirm}
            className={`rounded-md px-4 py-2 text-sm font-medium ${confirmButtonClass}`}
          >
            {confirmLabel || t("confirm.delete")}
          </button>
        </div>
      </div>
    </div>
  );
}
