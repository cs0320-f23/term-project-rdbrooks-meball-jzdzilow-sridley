import { atom } from "recoil";
import { IUser } from "../types/IUser";

export enum UserRole {
  Instructor = "instructor",
  DebuggingPartner = "debugging partner",
  HelpRequester = "help requester",
  NoneSelected = "",
}

export enum IssueType {
  Bug = "bug",
  ConceptualQuestion = "conceptual question",
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

export const issueTypeState = atom({
  key: "issueType",
  default: IssueType.NoneSelected,
});
