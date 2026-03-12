import { Conversation, Message, Participant } from "./types";
export declare class Client {
    private listeners;
    constructor(token: string);
    onTest(callback: (data: any) => void): any;
    onClientSynchronization(callback: (event: {
        status: string;
    }) => void): any;
    onConnectionStateChanged(callback: (event: {
        state: string;
    }) => void): any;
    onTokenExpired(callback: (message: string) => void): any;
    onTokenAboutToExpire(callback: (message: string) => void): any;
    onTypingStarted(callback: (participant: Participant) => void): any;
    onTypingEnded(callback: (participant: Participant) => void): any;
    onMessageAdded(callback: (message: Message) => void): any;
    getConversationBySid(sid: string): Promise<Conversation>;
    shutdown(): void;
}
//# sourceMappingURL=ExpoTwilioConversations.d.ts.map