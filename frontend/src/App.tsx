import { useState, useEffect } from 'react'
import { PlusCircle, Activity, ChevronRight } from 'lucide-react'
import WorkflowDetail from './WorkflowDetail'

function App() {
  const [workflows, setWorkflows] = useState<any[]>([])
  const [newReqs, setNewReqs] = useState('')
  const [activeWorkflow, setActiveWorkflow] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const fetchWorkflows = async () => {
    try {
      const res = await fetch('/api/workflows')
      const data = await res.json()
      setWorkflows(data)
    } catch (e) {
      console.error(e)
    }
  }

  useEffect(() => {
    fetchWorkflows()
    const interval = setInterval(fetchWorkflows, 3000)
    return () => clearInterval(interval)
  }, [])

  const startWorkflow = async () => {
    if (!newReqs.trim()) return
    setLoading(true)
    try {
      const res = await fetch('/api/workflows', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ requirements: newReqs })
      })
      const data = await res.json()
      setNewReqs('')
      setActiveWorkflow(data.workflowId)
      fetchWorkflows()
    } finally {
      setLoading(false)
    }
  }

  if (activeWorkflow) {
    return <WorkflowDetail id={activeWorkflow} onBack={() => setActiveWorkflow(null)} />
  }

  return (
    <div className="app-container">
      <header className="header">
        <h1>Agentic Orchestrator</h1>
        <p className="text-muted">Enterprise Multi-Agent SDLC Platform</p>
      </header>

      <div className="glass-panel" style={{ marginBottom: '40px' }}>
        <h2 style={{ marginBottom: '16px' }}>New Workflow</h2>
        <div style={{ display: 'flex', gap: '12px' }}>
          <input 
            type="text" 
            placeholder="e.g. Build a URL Shortener service with core APIs and analytics..."
            value={newReqs}
            onChange={e => setNewReqs(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && startWorkflow()}
          />
          <button className="btn" onClick={startWorkflow} disabled={loading}>
            {loading ? <Activity size={20} className="animate-spin" /> : <PlusCircle size={20} />}
            Start
          </button>
        </div>
      </div>

      <h2 style={{ marginBottom: '16px' }}>Active Workflows</h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {workflows.map(wf => (
          <div key={wf.id} className="glass-panel" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 24px' }}>
            <div>
              <div style={{ fontWeight: 500, marginBottom: '8px' }}>{wf.id}</div>
              <span className={`status-badge status-${wf.status}`}>{wf.status}</span>
            </div>
            <button className="btn btn-secondary" onClick={() => setActiveWorkflow(wf.id)}>
              View Details <ChevronRight size={16} />
            </button>
          </div>
        ))}
        {workflows.length === 0 && (
          <div className="glass-panel" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
            No workflows running. Start one above!
          </div>
        )}
      </div>
    </div>
  )
}

export default App
