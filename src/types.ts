export type Message = {
  body: string;
};

export type Participant = {
  sid: string;
  identity: string;
  attributes: string;
  conversation_sid: string;
};

export type Conversation = {
  typing: () => void;
};
