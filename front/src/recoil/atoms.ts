import { atom } from "recoil";
import { IUser } from "../types/IUser";

export enum UserRole {
  Instructor = "instructor",
  DebuggingPartner = "debugging-partner",
  HelpRequester = "help-requester",
  NoneSelected = "",
}
export const userState = atom({
  key: "userState",
  default: null as IUser | null,
});

export const userRoleState = atom({
  key: "userRoleState",
  default: { role: UserRole.NoneSelected, time: null as Date | null },
});

// allows changing between modes
export const mockedMode: boolean = false;
