type GraphProps = { data: { nodes: {id:string,label:string}[]; edges:{source:string;target:string}[] } | null };

export function Graph({ data }: GraphProps) {
  if (!data) return <div className="empty">Run an analysis to view the architecture graph.</div>;
  return (
    <div className="graph">
      {data.nodes.map((node) => {
        const outgoing = data.edges.filter(e => e.source === node.id);
        return (
          <div className="node" key={node.id}>
            <strong>{node.label}</strong>
            <small>{node.id}</small>
            {outgoing.length > 0 && <div className="links">→ {outgoing.map(e => data.nodes.find(n => n.id === e.target)?.label || e.target).join(", ")}</div>}
          </div>
        );
      })}
    </div>
  );
}
