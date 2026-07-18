import { useState, useEffect } from 'react'
import { ArrowLeft, CheckCircle, Code, FileText, Activity } from 'lucide-react'

export default function WorkflowDetail({ id, onBack }: { id: string, onBack: () => void }) {
  const [session, setSession] = useState<any>(null)
  const [humanInput, setHumanInput] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const fetchSession = async () => {
    try {
      const res = await fetch(`/api/workflows/${id}`)
      if (res.ok) {
        setSession(await res.json())
      }
    } catch (e) {
      console.error(e)
    }
  }

  useEffect(() => {
    fetchSession()
    const interval = setInterval(fetchSession, 2000)
    return () => clearInterval(interval)
  }, [id])

  const submitHumanInput = async () => {
    if (!humanInput.trim()) return
    setSubmitting(true)
    try {
      await fetch(`/api/workflows/${id}/resume`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: humanInput })
      })
      setHumanInput('')
      fetchSession()
    } finally {
      setSubmitting(false)
    }
  }

  if (!session) return <div className="app-container">Loading...</div>

  const isWaiting = session.status === 'WAITING_FOR_INPUT'
  const isAmbiguity = session.currentNodes?.includes('HUMAN_AMBIGUITY_INPUT')
  const isApproval = session.currentNodes?.includes('HUMAN_APPROVAL_GATE')

  return (
    <div className="app-container">
      <button className="btn btn-secondary" onClick={onBack} style={{ marginBottom: '24px' }}>
        <ArrowLeft size={20} /> Back to Dashboard
      </button>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px' }}>
        
        {/* Left Column: DAG Progress */}
        <div>
          <div className="glass-panel" style={{ marginBottom: '24px' }}>
            <h3 style={{ marginBottom: '16px' }}>Status: <span className={`status-badge status-${session.status}`}>{session.status}</span></h3>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              <div>Retries: {session.state?.retryCount}</div>
              <div>Rollbacks: {session.state?.rollbackCount}</div>
              <div>Iterations: {session.state?.dagIterationCount}</div>
            </div>
          </div>

          <div className="glass-panel">
            <h3 style={{ marginBottom: '16px' }}>Execution Graph</h3>
            {session.currentNodes?.map((node: string) => (
              <div key={node} className="dag-node active">
                <Activity size={16} color="var(--accent)" />
                <span>{node}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Right Column: Artifacts & Logs */}
        <div className="glass-panel">
          <h3 style={{ marginBottom: '16px' }}>Code Artifacts</h3>
          {Object.entries(session.state?.codeArtifacts || {}).map(([filename, content]: [string, any]) => (
            <div key={filename} style={{ marginBottom: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                {filename.endsWith('.md') ? <FileText size={16} /> : <Code size={16} />}
                <strong style={{ color: 'var(--accent)' }}>{filename}</strong>
              </div>
              <pre style={{ background: 'rgba(0,0,0,0.3)', padding: '16px', borderRadius: '8px', overflowX: 'auto', fontSize: '0.875rem' }}>
                {content}
              </pre>
            </div>
          ))}
          {Object.keys(session.state?.codeArtifacts || {}).length === 0 && (
            <p style={{ color: 'var(--text-muted)' }}>No artifacts generated yet...</p>
          )}
        </div>
      </div>

      {/* Human In The Loop Modal */}
      {isWaiting && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content">
            <h2 style={{ marginBottom: '16px', color: 'var(--warning)' }}>
              Human Intervention Required
            </h2>
            <p style={{ marginBottom: '24px', color: 'var(--text-muted)' }}>
              {isAmbiguity && "The agent found the requirements ambiguous. Please clarify."}
              {isApproval && "Please review the Architecture.md artifact and provide your approval."}
            </p>
            <textarea 
              rows={4}
              placeholder={isApproval ? "Looks good, approved." : "Clarification details..."}
              value={humanInput}
              onChange={e => setHumanInput(e.target.value)}
              style={{ marginBottom: '16px' }}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button className="btn" onClick={submitHumanInput} disabled={submitting || !humanInput.trim()}>
                <CheckCircle size={20} /> Submit & Resume
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
