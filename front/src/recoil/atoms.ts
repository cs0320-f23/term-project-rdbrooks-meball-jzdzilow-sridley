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

export enum IssueType {
  Bug = "bug",
  ConceptualQuestion = "conceptual question",
  NoneSelected = "",
}

export const userSessionState = atom({
  key: "userSessionState",
  default: {
    user: null as IUser | null,
    role: UserRole.NoneSelected,
    time: null as Date | null,
  },
});

export const singleSessionState = atom({
  key: "singleSessionState",
  default: { partner: null as IUser | null, issueType: IssueType.NoneSelected },
});

// allows changing between modes
export const mockedMode: boolean = false;
