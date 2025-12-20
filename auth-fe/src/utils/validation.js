// At least 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char. Example: Password@1234lpc
export const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
