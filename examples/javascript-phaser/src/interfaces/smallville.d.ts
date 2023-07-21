declare module 'smallville' {
    export type SmallvilleState = { agents: Agent[], locations: Location[], conversations: Conversation[] }
    export type StateHandler = (state: SmallvilleState) => void
    export type Agent = {name: string, activity: string, location: string}
    export type Location = {name: string, state: string}
    export type Conversation = {agent: string, other?: string, dialog: Dialog[]}
    export type Dialog = {agent: string, message: string}

    export class Smallville {
        constructor({ host="http://localhost:8080/", stateHandler }: {
            host: string,
            stateHandler: StateHandler
        });

        sync(): void;
        init(): Promise<boolean>;
        createAgent({ name, memories, location, activity }: {
            name: string,
            memories: string[],
            location: string,
            activity: string
        }): Promise<boolean>;
        createLocation({ name }: { name: string }): Promise<boolean>;
        updateState(): Promise<SmallvilleState>
        addObservation({ name, observation, reactable = true }: { name: string, observation: string, reactable: boolean }): Promise<boolean>
        ask({ name, question }: { name: string, question: string }): Promise<string>
    }
}