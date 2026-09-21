import React from 'react';
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-react';

export default function StatusAlert({ type = 'error', message, details = [], onClose }) {
  if (!message) return null;

  const isError = type === 'error';
  const isSuccess = type === 'success';

  return (
    <div
      className={`relative rounded-xl p-4 border text-sm transition-all duration-300 shadow-lg ${
        isError
          ? 'bg-rose-950/40 border-rose-800/60 text-rose-200'
          : isSuccess
          ? 'bg-emerald-950/40 border-emerald-800/60 text-emerald-200'
          : 'bg-sky-950/40 border-sky-800/60 text-sky-200'
      }`}
    >
      <div className="flex items-start gap-3">
        {isError && <AlertCircle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />}
        {isSuccess && <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />}
        {!isError && !isSuccess && <Info className="w-5 h-5 text-sky-400 shrink-0 mt-0.5" />}

        <div className="flex-1 pr-6">
          <p className="font-medium leading-relaxed">{message}</p>
          {details && details.length > 0 && (
            <ul className="mt-2 list-disc list-inside space-y-1 text-xs opacity-90">
              {details.map((d, idx) => (
                <li key={idx}>
                  <span className="font-semibold">{d.field}:</span> {d.issue}
                </li>
              ))}
            </ul>
          )}
        </div>

        {onClose && (
          <button
            onClick={onClose}
            className="absolute top-3 right-3 p-1 rounded-lg hover:bg-white/10 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>
    </div>
  );
}
