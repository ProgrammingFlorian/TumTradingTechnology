// String format for all numbers displayed as money amounts
const currency: Intl.NumberFormatOptions = {
    currency: "USD",
    style: "currency",
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
    minimumIntegerDigits: 2,
};


// String format for small numbers displayed as percentages
const percent: Intl.NumberFormatOptions = {
    style: "decimal",
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
    minimumIntegerDigits: 1,
};

// String format for big numbers displayed as percentages
const percentBigNumber: Intl.NumberFormatOptions = {
    style: "decimal",
    maximumFractionDigits: 1,
    minimumFractionDigits: 1,
    minimumIntegerDigits: 1,
};

export const Formats = {
    currency,
    percent,
    percentBigNumber
};