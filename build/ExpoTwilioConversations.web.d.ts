import { Conversation, Message, Participant } from "./types";
export declare class Client {
    private client;
    constructor(token: string);
    onTest(callback: (data: any) => void): void;
    onClientSynchronization(callback: (event: {
        status: string;
    }) => void): void;
    onConnectionStateChanged(callback: (event: {
        state: string;
    }) => void): void;
    onTokenExpired(callback: (message: string) => void): void;
    onTokenAboutToExpire(callback: (message: string) => void): void;
    onTypingStarted(callback: (participant: Participant) => void): void;
    onTypingEnded(callback: (participant: Participant) => void): void;
    onMessageAdded(callback: (message: Message) => void): void;
    getConversationBySid(sid: string): Promise<Conversation>;
    shutdown(): void;
}
//# sourceMappingURL=ExpoTwilioConversations.web.d.ts.map