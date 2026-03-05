export declare class Client {
    private token;
    constructor(token: string);
    on(event: string, callback: (...args: any[]) => void): void;
    shutdown(): void;
}
//# sourceMappingURL=ExpoTwilioConversations.d.ts.map