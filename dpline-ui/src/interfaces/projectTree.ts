export interface ProjectTree {
    id: string;
    name: string;
    code: string;
    children?: ProjectTree[];
}