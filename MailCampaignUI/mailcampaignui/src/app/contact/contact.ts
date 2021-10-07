export interface ContactListResponse {
  data: Contact[];
  totalElements: number;
}
export interface Contact {
  pk: number;
  fullName: string;
  email: string;
  emailSent: boolean;
  howLong: string;
  isEmailDone: boolean;
}
